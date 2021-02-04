package com.innowise.duvalov.util;

import com.innowise.duvalov.pool.ConnectionPool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public final class PropertyHelper {
    private static final Logger LOGGER = Logger.getLogger(PropertyHelper.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var inputStream = PropertyHelper.
                class.getClassLoader().
                getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}