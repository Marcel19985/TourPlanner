package org.example.tourplanner.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HtmlTemplateLoader {
    private static String API_KEY; //API key von OpenRouteService wird verwendet, um HTTP Request durchzuführen
    static { //static wird ausgeführt, bevor eine Instanz der Klasse erstellt wird
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/application.properties"));
            API_KEY = properties.getProperty("ors.api.key"); //API Schlüssel aus Konfig Datei speichern
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Diese Methode lädt die HTML-Datei und ersetzt die Platzhalter durch die Werte
    public static String loadTourMapHtml(String startCoords, String destCoords, String transportMode) throws IOException {
        String[] startCoordArray = startCoords.split(",");
        String[] destCoordArray = destCoords.split(",");
        // Pfad zur HTML-Datei im Resources-Ordner
        String filePath = "/tour_map.html";

        // Lade die HTML-Datei
        InputStream inputStream = HtmlTemplateLoader.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("HTML-Datei konnte nicht gefunden werden");
        }

        // Lese den Inhalt der Datei
        StringBuilder htmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line).append("\n");
            }
        }
        String orsMode = switch (transportMode.toLowerCase()) { //Transportmittel bestimmen: kann man eleganter machen; funktion mit ChatGPT generiert
            case "car" -> "driving-car";
            case "bike" -> "cycling-regular";
            case "walk" -> "foot-walking";
            default -> throw new IllegalArgumentException("Ungültiges Transportmittel: " + transportMode);
        };

        // Ersetze Platzhalter durch tatsächliche Werte
        String html = htmlContent.toString();
        html = html.replace("{{startLat}}", String.valueOf(startCoordArray[1]))
                .replace("{{startLon}}", String.valueOf(startCoordArray[0]))
                .replace("{{destLat}}", String.valueOf(destCoordArray[1]))
                .replace("{{destLon}}", String.valueOf(destCoordArray[0]))
                .replace("{{apiKey}}", API_KEY)
                .replace("{{transportMode}}", orsMode);


        return html;
    }
}
