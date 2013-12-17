package com.alexkasko.installer.testapp;

import com.alexkasko.installer.DaemonLauncher;

import java.util.Date;

/**
 * @author alexkasko
 * Date: 4/30/12
 */
public class Launcher implements DaemonLauncher {
    private static final Thread BACKGROUND_THREAD = new Thread(new BackgroundWork());

    public void startDaemon() {
            BACKGROUND_THREAD.setDaemon(true);
            BACKGROUND_THREAD.start();
    }

    public void stopDaemon() {
        BACKGROUND_THREAD.interrupt();
    }

    private static class BackgroundWork implements Runnable {
        public void run() {
            for (;;) {
                if (Thread.interrupted()) break;
                System.out.println(new Date());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    // not required for windows service, added for direct launch
    public static void main(String[] args) throws InterruptedException {
        BACKGROUND_THREAD.setDaemon(true);
        BACKGROUND_THREAD.start();
        for(;;) {
            Thread.sleep(10000);
            System.out.println("Main thread is alive");
        }
    }
}
