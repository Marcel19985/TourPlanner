package org.example.tourplanner.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class Database {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private static Connection connection = null;

    // Methode zum Laden der Konfigurationsdatei
    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Methode zum Herstellen der DB-Verbindung
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            Properties config = loadConfig();
            String url = config.getProperty("db.url");
            String username = config.getProperty("db.username");
            String password = config.getProperty("db.password");
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    // Optional: Methode zum Schließen der Verbindung
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
