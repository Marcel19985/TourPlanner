package org.example.tourplanner.dataLayer.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    private static final String URL = "jdbc:postgresql://localhost:5432/tourplanner"; // DB-Name anpassen
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345"; // Dein Passwort

    private static Connection connection;

    @BeforeAll
    static void setup() {
        try {
            Class.forName("org.postgresql.Driver"); // Treiber laden
        } catch (ClassNotFoundException e) {
            fail("JDBC-Treiber nicht gefunden: " + e.getMessage());
        }
    }

    @Test
    void testDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            assertNotNull(connection, "Die Verbindung zur Datenbank sollte nicht null sein");
            assertFalse(connection.isClosed(), "Die Verbindung sollte geöffnet sein");
        } catch (SQLException e) {
            fail("Fehler beim Herstellen der Verbindung: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        if (connection != null) {
            try {
                connection.close();
                assertTrue(connection.isClosed(), "Die Verbindung sollte geschlossen sein");
            } catch (SQLException e) {
                fail("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        }
    }
}
