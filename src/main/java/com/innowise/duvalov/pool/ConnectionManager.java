package com.innowise.duvalov.pool;

import com.innowise.duvalov.util.PropertyHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String USER_KEY = "User";
    private static final String PASSWORD_KEY = "Pass";
    private static final String URL_KEY = "URL";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PropertyHelper.get(USER_KEY),
                PropertyHelper.get(PASSWORD_KEY),
                PropertyHelper.get(URL_KEY));
    }

    public static ProxyConnection getProxyConnection() throws SQLException {
        return new ProxyConnection(getConnection());
    }
}
