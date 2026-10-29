package com.frescuratotal.erp.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton that provides JDBC connections to the MySQL database.
 *
 * <p><b>Design pattern: Singleton.</b> A single instance of this class exists
 * per JVM, guaranteeing a single, centrally-configured entry point to obtain
 * database connections. This avoids scattering {@code DriverManager} logic
 * across the codebase and keeps connection handling in one place (SRP).</p>
 */
public final class DatabaseConnection {

    /** The unique instance (eager initialization, thread-safe by class loading). */
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        // Load the MySQL JDBC driver once.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "MySQL JDBC driver not found on classpath.", ex);
        }
    }

    /**
     * Returns the unique instance of the connection manager.
     *
     * @return the singleton instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Opens a new JDBC connection using the centralized {@link DatabaseConfig}.
     *
     * @return an open {@link Connection}
     * @throws SQLException if the connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getJdbcUrl(),
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD);
    }
}
