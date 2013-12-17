package com.alexkasko.installer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Actual daemon launcher that will delegate application startup/shutdown to client launcher
 *
 * @author alexkasko
 * Date: 5/2/12
 */
public class StandardLauncher {
    // AtomicReference just in case, daemon should never be started or stopped more than once during JVM lifetime
    private static final AtomicReference<DaemonLauncher> HOLDER = new AtomicReference<DaemonLauncher>();
    private static final CountDownLatch LATCH = new CountDownLatch(1);

    /**
     * Daemonized java process entry point
     *
     * @param args there must be exactly 2 parameters, 1st - 'start' ot 'stop', second - 'launcherClassName'
     */
    public static void main(String[] args) {
        if (2 != args.length) throw new IllegalArgumentException("No argument provided, must either 'start' or 'stop' and 'launcherClassName'");
        String command = args[0];
        String className = args[1];
        System.out.println("Running daemon command: '" + command + "' with main class: " + className);
        if ("start".equals(command)) {
            start(className);
            System.out.println("Daemon started");
            awaitOnLatch();
        } else if ("stop".equals(args[0])) {
            stop();
            LATCH.countDown();
            System.out.println("Daemon stopped");
        } else {
            throw new IllegalArgumentException("Argument must be either 'start' or 'stop'");
        }
    }

    // separate methods for proper error reporting
    private static void start(String className) {
        try {
            // static methods cannot be overridden, so we need an instance
            Class clazz = Class.forName(className);
            DaemonLauncher instance = (DaemonLauncher) clazz.newInstance();
            DaemonLauncher existed = HOLDER.getAndSet(instance);
            if (null != existed) throw new IllegalStateException("Daemon is already started");
            // http://issues.apache.org/jira/browse/DAEMON-100
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
            instance.startDaemon();
        } catch (Exception e) {
            e.printStackTrace();
            // rethrow startup exception to stop JVM
            throw new RuntimeException(e);
        }
    }

    private static void stop() {
        DaemonLauncher instance = HOLDER.get();
        if (null == instance) {
            System.err.println("Cannot stop daemon because it wasn't started");
            throw new IllegalStateException("Daemon wasn't started");
        }
        try {
            instance.stopDaemon();
        } catch (Exception e) {
            // not rethrow shutdown exception
            e.printStackTrace();
        }
    }

    private static void awaitOnLatch() {
        try {
            LATCH.await();
        } catch (InterruptedException e) {
            System.out.println("Daemon interrupted, exiting...");
            stop();
        }
    }
}
