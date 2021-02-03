package com.innowise.duvalov.СustomConnection;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public enum ConnectionPool {
    INSTANCE;

    private static boolean isCreated = false;
    private static final int DEFAULT_POOL_SIZE = 20;
    private BlockingQueue<ProxyConnection> availableConnection;
    private List<ProxyConnection> takenConnections;

    ConnectionPool() {
        availableConnection = new LinkedBlockingDeque<>();
        takenConnections = new LinkedList<>();
    }

    public void createPool() throws SQLException {
        if (!isCreated) {
            for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
                availableConnection.add(ConnectionManager.getProxyConnection());
            }
            isCreated = true;
        }
    }

    public ProxyConnection getConnection() {
        ProxyConnection connection = null;
        try {
            connection = availableConnection.take();
            takenConnections.add(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void releaseConnection(ProxyConnection connection) {
        if (connection != null) {
            availableConnection.add(connection);
            takenConnections.remove(connection);
        }
    }

    public void deletePool() throws SQLException {
        if (!takenConnections.isEmpty()) {
            System.out.println("Some connections were not released");
            for (ProxyConnection connection : takenConnections) {
                releaseConnection(connection);
            }
        }
        for (ProxyConnection connection : availableConnection) {
            connection.close();
        }

    }
}
