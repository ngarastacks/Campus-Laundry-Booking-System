package com.campuslaundry.repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens connections to the MySQL database. Settings are read from
 * db.properties in the project folder, so passwords stay out of the code
 * (and out of GitHub).
 */
public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/campus_laundry?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final Properties settings = loadSettings();

    private DatabaseConnection() { }

    private static Properties loadSettings() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("db.properties not found - using defaults (user root, empty password).");
        }
        return p;
    }

    /** Returns a new connection. Callers close it with try-with-resources. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                settings.getProperty("db.url", DEFAULT_URL),
                settings.getProperty("db.user", "root"),
                settings.getProperty("db.password", ""));
    }
}