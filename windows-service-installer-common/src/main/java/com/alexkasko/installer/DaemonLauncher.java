package com.alexkasko.installer;

/**
 * Interface for clients launched class to daemonize
 *
 * @author alexkasko
 * Date: 5/4/12
 */
public interface DaemonLauncher {
    /**
     * Starts application, must not block, will be called no more than once
     */
    void startDaemon();

    /**
     * Starts application, must not block, will be called no more than once
     */
    void stopDaemon();
}
