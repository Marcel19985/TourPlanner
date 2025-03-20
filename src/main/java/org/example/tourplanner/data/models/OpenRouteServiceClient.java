package org.example.tourplanner.data.models;

import org.example.tourplanner.helpers.LocationNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Properties;
import java.io.FileInputStream;

//Folgende Klasse wurde teilweise mit AI generiert
public class OpenRouteServiceClient {

    private static String API_KEY; //API key von OpenRouteService wird verwendet, um HTTP Request durchzuführen
    static { //static wird ausgeführt, bevor eine Instanz der Klasse erstellt wird
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            API_KEY = properties.getProperty("ors.api.key"); //API Schlüssel aus Konfig Datei speichern
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[] getRouteDetails(String start, String destination, String transportType) throws IOException, JSONException, LocationNotFoundException {
        //Koordinaten von Start und Zielort abfragen:
        String startCoords = getCoordinates(start);
        String destCoords = getCoordinates(destination);

        String orsMode = switch (transportType.toLowerCase()) { //Transportmittel bestimmen: kann man eleganter machen; funktion mit ChatGPT generiert
            case "car" -> "driving-car";
            case "bike" -> "cycling-regular";
            case "walk" -> "foot-walking";
            default -> throw new IllegalArgumentException("Ungültiges Transportmittel: " + transportType);
        };

        //URL für route request zusammenbauen: Beispielrequests siehe hier: https://openrouteservice.org/dev/#/api-docs
        String urlString = "https://api.openrouteservice.org/v2/directions/" + orsMode +
                "?api_key=" + API_KEY +
                "&start=" + startCoords +
                "&end=" + destCoords;

        URL url = new URL(urlString); //neues URL Objekt; Code Block mit ChatGPT generiert
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        //http header definieren:
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("User-Agent", "Java-Client"); //Server bekommt Info, dass Anfrage von Java Anwendung kommt

        if (conn.getResponseCode() != 200) { //Fehlerhafte Antwort vom Server
            throw new IOException("Fehler bei der API-Anfrage: HTTP " + conn.getResponseCode());
        }

        //Serverantwort als JSON einlesen:
        Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
        String jsonResponse = scanner.useDelimiter("\\A").next(); //durch ("\\A").next() wird komplette JSON Antwort als STRING gespeichert
        scanner.close();
        conn.disconnect();

        return parseRouteDetails(jsonResponse); //siehe Methode darunter
    }

    private static double[] parseRouteDetails(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);
        JSONArray features = json.getJSONArray("features"); //erstellt ein JSON Array aus String
        if (features.length() == 0) {
            throw new RuntimeException("Keine Route gefunden");
        }
        JSONObject properties = features.getJSONObject(0).getJSONObject("properties");
        JSONObject summary = properties.getJSONObject("summary");
        double distance = summary.getDouble("distance") / 1000; //in km umrechnen
        double duration = summary.getDouble("duration") / 60; //in Minuten umrechnen

        return new double[]{distance, duration};
    }

    private static String getCoordinates(String location) throws IOException, JSONException, LocationNotFoundException { //Wandelt einen Ort in dessen Koordinaten um
        String urlString = "https://api.openrouteservice.org/geocode/search?api_key=" + API_KEY +
                "&text=" + location.replace(" ", "%20") + "&size=1"; //Leerzeichen werden durch %20 ersetzt

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            System.err.println("Fehler beim Geocoding: HTTP " + conn.getResponseCode());
            throw new IOException("Fehler beim Geocoding: HTTP " + conn.getResponseCode());
        }

        //Liest die Antwort der Geocoding-API als JSON-String ein:
        Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
        String jsonResponse = scanner.useDelimiter("\\A").next();
        scanner.close();
        conn.disconnect();

        JSONObject json = new JSONObject(jsonResponse); //Erstellt ein JSONObject aus der Antwort.
        JSONArray features = json.getJSONArray("features"); //Erstellt ein feature Array

        if (features.length() == 0) {
            System.err.println("Location not found " + location);
            throw new LocationNotFoundException("Location not found: " + location);
        }

        //API liefert Koordinaten im Format "lon,lat":
        JSONArray coordinates = features.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
        String coords = coordinates.getDouble(0) + "," + coordinates.getDouble(1);

        return coords;
    }
}
