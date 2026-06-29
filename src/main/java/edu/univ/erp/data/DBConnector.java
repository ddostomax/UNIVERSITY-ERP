package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Creates and holds pooled DataSource instances for Auth DB and ERP DB.
 *
 * Configuration is loaded from db.properties on the classpath.
 */
public final class DBConnector {

    private static DataSource authDataSource;
    private static DataSource erpDataSource;

    private DBConnector() {
    }

    public static synchronized void init() {
        if (authDataSource != null && erpDataSource != null) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream in = DBConnector.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }

        authDataSource = createDataSource(
                properties.getProperty("auth.jdbc.url"),
                properties.getProperty("auth.jdbc.user"),
                properties.getProperty("auth.jdbc.password")
        );

        erpDataSource = createDataSource(
                properties.getProperty("erp.jdbc.url"),
                properties.getProperty("erp.jdbc.user"),
                properties.getProperty("erp.jdbc.password")
        );
    }

    private static DataSource createDataSource(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("ERP-Pool");
        return new HikariDataSource(config);
    }

    public static DataSource getAuthDataSource() {
        if (authDataSource == null) {
            init();
        }
        return authDataSource;
    }

    public static DataSource getErpDataSource() {
        if (erpDataSource == null) {
            init();
        }
        return erpDataSource;
    }
}


