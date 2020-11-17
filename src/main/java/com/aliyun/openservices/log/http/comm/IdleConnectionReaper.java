package com.aliyun.openservices.log.http.comm;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

/**
 * A daemon thread used to periodically check connection pools for idle
 * connections.
 */
public final class IdleConnectionReaper extends Thread {
    private static final int REAP_INTERVAL_MILLISECONDS = 5 * 1000;
    private static final ArrayList<HttpClientConnectionManager> connectionManagers = new ArrayList<HttpClientConnectionManager>();

    private static IdleConnectionReaper instance;
    private static long idleConnectionTime = 60 * 1000;
    private volatile boolean shuttingDown = false;

    private IdleConnectionReaper() {
        super("idle_connection_reaper");
        setDaemon(true);
    }

    public static synchronized void registerConnectionManager(HttpClientConnectionManager connectionManager) {
        if (instance == null) {
            instance = new IdleConnectionReaper();
            instance.start();
        }
        connectionManagers.add(connectionManager);
    }

    public static synchronized void removeConnectionManager(HttpClientConnectionManager connectionManager) {
        connectionManagers.remove(connectionManager);
        if (connectionManagers.isEmpty()) {
            shutdown();
        }
    }

    private void markShuttingDown() {
        shuttingDown = true;
    }

    @Override
    public void run() {
        while (!shuttingDown) {
            try {
                Thread.sleep(REAP_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            try {
                List<HttpClientConnectionManager> connectionManagers = null;
                synchronized (IdleConnectionReaper.class) {
                    connectionManagers = (List<HttpClientConnectionManager>) IdleConnectionReaper.connectionManagers
                            .clone();
                }
                for (HttpClientConnectionManager connectionManager : connectionManagers) {
                    try {
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(idleConnectionTime, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            instance.markShuttingDown();
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
        }
    }

    public static synchronized void setIdleConnectionTime(long idletime) {
        idleConnectionTime = idletime;
    }

}
