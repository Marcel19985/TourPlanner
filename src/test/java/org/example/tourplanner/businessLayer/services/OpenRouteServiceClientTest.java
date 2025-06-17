package org.example.tourplanner.businessLayer.services;
import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.helpers.LocationNotFoundException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;


class OpenRouteServiceClientTest {

    @Test
    void testParseRouteDetails_validJson() throws JSONException { //Testet, ob aus JSON die Koordinaten korrekt ausgelesen werden
        String json = "{\n" +
                "  \"features\": [\n" +
                "    {\n" +
                "      \"properties\": {\n" +
                "        \"summary\": {\n" +
                "          \"distance\": 2500.0,\n" +
                "          \"duration\": 1800.0\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        double[] result = OpenRouteServiceClient.parseRouteDetails(json);
        assertEquals(2.5, result[0], 1e-6, "Distance should be converted to kilometers"); //Distance/ 1000 für Kilometer
        assertEquals(30.0, result[1], 1e-6, "Duration should be converted to minutes"); //Duration / 60 für Stunden
    }

    @Test
    void testParseRouteDetails_emptyFeatures() { //Testet, ob eine Exception geworfen wird, wenn keine Route gefunden wurde
        String json = "{\"features\": []}";
        Executable call = () -> OpenRouteServiceClient.parseRouteDetails(json);
        RuntimeException ex = assertThrows(RuntimeException.class, call);
        assertEquals("No route found", ex.getMessage());
    }

    @Test
    void testGetRouteDetails_noRealHttpCall() throws Exception { //Dieser Test wurde mit ChatGPT 4o generiert und danch adaptiert
        //Erstelle Tour mit Dummy-Daten
        Tour tour = new Tour("T", "D", "StartLoc", "EndLoc", "car");

        //Mocke statische Methoden, aber rufe CALLS_REAL_METHODS, damit getRouteDetails weiterläuft -> alle Methoden, die nicht explizit gemockt werden, rufen die echte Implementierung auf:
        try (MockedStatic<OpenRouteServiceClient> staticMock =
                     mockStatic(OpenRouteServiceClient.class, CALLS_REAL_METHODS)) {

            staticMock.when(() -> OpenRouteServiceClient.getCoordinates("StartLoc"))
                    .thenReturn("1.0,2.0");
            staticMock.when(() -> OpenRouteServiceClient.getCoordinates("EndLoc"))
                    .thenReturn("3.0,4.0");
            //parseRouteDetails soll nie echt aufgerufen werden
            staticMock.when(() -> OpenRouteServiceClient.parseRouteDetails(anyString()))
                    .thenReturn(new double[]{7.7, 17.7});

            //Mocke jeden new URL(...) Aufruf
            try (MockedConstruction<URL> urlMock = mockConstruction(URL.class, (mockUrl, ctx) -> {
                //jedes Mal wenn openConnection() gerufen wird:
                HttpURLConnection fakeConn = mock(HttpURLConnection.class);
                when(mockUrl.openConnection()).thenReturn(fakeConn);
                //Simuliere HTTP 200 OK
                when(fakeConn.getResponseCode()).thenReturn(200);
                //Leere JSON-Antwort (wird ja nicht geparst, weil wir stubben)
                when(fakeConn.getInputStream())
                        .thenReturn(new ByteArrayInputStream("{}".getBytes()));
            })) {
                //Methode ausführen – kein echter HTTP-Request
                Tour updated = OpenRouteServiceClient.getRouteDetails(tour);

                //Assertions
                assertSame(tour, updated, "Soll dasselbe Objekt sein");
                assertEquals(7.7, updated.getDistance(),       1e-6);
                assertEquals(17.7, updated.getEstimatedTime(), 1e-6);

                //Verifizieren, dass Stubs genutzt wurden
                staticMock.verify(() -> OpenRouteServiceClient.getCoordinates("StartLoc"));
                staticMock.verify(() -> OpenRouteServiceClient.getCoordinates("EndLoc"));
                staticMock.verify(() -> OpenRouteServiceClient.parseRouteDetails(anyString()));
            }
        }
    }
}