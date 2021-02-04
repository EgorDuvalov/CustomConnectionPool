package com.innowise.duvalov.pool;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public enum ConnectionPool {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class);

    private static final AtomicBoolean isCreated = new AtomicBoolean(false);
    private static final int DEFAULT_POOL_SIZE = 20;

    private final BlockingQueue<ProxyConnection> availableConnection;
    private final List<ProxyConnection> takenConnections;

    ConnectionPool() {
        availableConnection = new LinkedBlockingDeque<>();
        takenConnections = new LinkedList<>();
    }

    public void createPool() {
        if (!isCreated.get()) {
            for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
                try {
                    availableConnection.add(ConnectionManager.getProxyConnection());
                } catch (SQLException throwables) {
                    LOGGER.error(throwables);
                }
            }
            isCreated.set(true);
        }
    }

    public ProxyConnection getConnection() {
        ProxyConnection connection = null;
        try {
            connection = availableConnection.take();
            takenConnections.add(connection);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }
        return connection;
    }

    public void releaseConnection(ProxyConnection connection) {
        if (connection != null) {
            takenConnections.remove(connection);
            boolean isReturned = availableConnection.add(connection);
            if (isReturned) {
                LOGGER.warn("Connection can't be added to available ones");
            }
        }
    }

    public void clearPool() throws SQLException {
        if (!takenConnections.isEmpty()) {
            LOGGER.warn("Some connections weren't released before cleaning");
            for (ProxyConnection connection : takenConnections) {
                releaseConnection(connection);
            }
        }
        for (ProxyConnection connection : availableConnection) {
            connection.close();
        }
        isCreated.set(false);
    }
}
