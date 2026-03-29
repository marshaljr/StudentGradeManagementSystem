package com.sms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConfig — Singleton pattern.
 *
 * Ensures only ONE database connection is created for the lifetime
 * of the application.  All DAO classes obtain the connection through
 * DatabaseConfig.getInstance().getConnection().
 *
 * ----------------------------------------------------------------
 * OOP Principle: Singleton (creational design pattern)
 * ----------------------------------------------------------------
 */
public class DatabaseConfig {

    // ── Connection settings ────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/student_grade_db"
                                           + "?useSSL=false&serverTimezone=UTC"
                                           + "&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";        // ← change if needed
    private static final String PASSWORD = "root"; // ← change to your MySQL password

    // ── Singleton instance ─────────────────────────────────────────
    private static DatabaseConfig instance;

    // ── Connection object ──────────────────────────────────────────
    private Connection connection;

    /** Private constructor — prevents external instantiation. */
    private DatabaseConfig() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
    }

    /**
     * Returns the single instance of DatabaseConfig.
     * Thread-safe via synchronized keyword (suitable for desktop app).
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Returns the active JDBC Connection.
     * Re-creates it if closed or null.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Could not reopen connection: " + e.getMessage());
        }
        return connection;
    }

    /** Closes the connection gracefully on application exit. */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
