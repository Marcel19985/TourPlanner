package org.example.tourplanner.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
        try {
            // Manuelles Laden des JDBC-Treibers
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://localhost:5432/tourplanner"; // Ersetze mit deinem DB-Namen
            String user = "postgres";
            String password = "12345"; // Ersetze mit deinem Passwort

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                if (connection != null) {
                    System.out.println("Verbindung erfolgreich!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Fehler bei der Verbindung: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC-Treiber nicht gefunden!");
        }
    }
}
