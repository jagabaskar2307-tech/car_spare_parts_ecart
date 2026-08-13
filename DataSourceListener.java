package com.jagadeesh.jagadeeshcart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * Owns the single HikariCP connection pool for the application (Singleton pattern).
 * No other class should call DriverManager.getConnection() directly.
 * On startup, also runs schema.sql once (idempotent, uses CREATE TABLE IF NOT EXISTS).
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(DataSourceListener.class);
    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            log.error("Failed to load application.properties", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url", "jdbc:h2:./data/jagadeeshcart;AUTO_SERVER=TRUE"));
        config.setUsername(props.getProperty("db.user", "sa"));
        config.setPassword(props.getProperty("db.password", ""));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
        config.setDriverClassName("org.h2.Driver");

        dataSource = new HikariDataSource(config);
        log.info("HikariCP connection pool initialized");

        runSchema();
    }

    private void runSchema() {
        try (Connection conn = dataSource.getConnection();
             InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                log.warn("schema.sql not found on classpath; skipping auto-init");
                return;
            }
            String sql = new String(in.readAllBytes());
            try (Statement stmt = conn.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
            log.info("Schema verified/initialized");
        } catch (Exception e) {
            log.error("Failed to run schema.sql", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            log.info("HikariCP connection pool closed");
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
