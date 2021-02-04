package com.innowise.duvalov.pool;

import com.innowise.duvalov.util.PropertyHelper;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class);

    private static final String USER_KEY = "User";
    private static final String PASSWORD_KEY = "Pass";
    private static final String URL_KEY = "URL";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    PropertyHelper.get(USER_KEY),
                    PropertyHelper.get(PASSWORD_KEY),
                    PropertyHelper.get(URL_KEY));
        } catch (SQLException throwables) {
            LOGGER.error(throwables);
        }
        return connection;
    }

    public static ProxyConnection getProxyConnection() {
        return new ProxyConnection(getConnection());
    }
}
