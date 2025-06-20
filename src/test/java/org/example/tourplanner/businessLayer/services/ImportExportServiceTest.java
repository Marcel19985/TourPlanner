package org.example.tourplanner.businessLayer.services;

import org.example.tourplanner.businessLayer.models.Tour;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ImportExportServiceTest {

    private ImportExportService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new ImportExportService();
        cleanUp();
    }


    @AfterEach
    void cleanUp() throws IOException {
        // Lösche alle Testdateien im Export-Ordner
        Path exportDir = Paths.get("import_export_json");
        if (Files.exists(exportDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(exportDir, "tours_export_*.json")) {
                for (Path entry : stream) {
                    Files.deleteIfExists(entry);
                }
            }
        }
    }

    @Test
    void testExportToursToJsonAndImportToursFromJson() throws IOException {
        Tour tour1 = new Tour("TestTour1", "desc", "start", "dest", "car", 10, 20);
        Tour tour2 = new Tour("TestTour2", "desc", "start", "dest", "car", 10, 20);

        List<Tour> tours = Arrays.asList(tour1, tour2);

        service.exportToursToJson(tours);

        // Finde die erzeugte Datei
        File exportDir = new File("import_export_json");
        File[] files = exportDir.listFiles((dir, name) -> name.startsWith("tours_export_") && name.endsWith(".json"));
        assertNotNull(files);
        assertTrue(files.length > 0);

        File jsonFile = files[0];
        List<Tour> importedTours = service.importToursFromJson(jsonFile);

        assertEquals(2, importedTours.size());
        assertTrue(importedTours.stream().anyMatch(t -> "TestTour1".equals(t.getName())));
        assertTrue(importedTours.stream().anyMatch(t -> "TestTour2".equals(t.getName())));
    }

    @Test
    void testExportToursToJsonWithDuplicates() throws IOException {
        Tour tour1 = new Tour("TourA", "desc", "start", "dest", "car", 10, 20);
        Tour tour2 = new Tour("TourA", "desc", "start", "dest", "car", 10, 20);

        List<Tour> tours = Arrays.asList(tour1, tour2);

        service.exportToursToJson(tours);

        File exportDir = new File("import_export_json");
        File[] files = exportDir.listFiles((dir, name) -> name.startsWith("tours_export_") && name.endsWith(".json"));
        assertNotNull(files);
        assertTrue(files.length > 0);

        File jsonFile = files[0];
        List<Tour> importedTours = service.importToursFromJson(jsonFile);

        // Es sollte nur eine Tour importiert werden, da Duplikate entfernt werden
        assertEquals(1, importedTours.size());
        assertEquals("TourA", importedTours.get(0).getName());
    }

    @Test
    void testImportToursFromJsonFile() throws IOException {
        // Erstelle eine temporäre JSON-Datei mit einer Tour
        String json = """
            [
              {
                "name": "ImportedTour",
                "description": "import desc",
                "start": "importStart",
                "destination": "importDest",
                "transportType": "bike",
                "distance": 42.0,
                "estimatedTime": 123.0
              }
            ]
            """;
        Path tempDir = Files.createTempDirectory("tour_import_test");
        File jsonFile = tempDir.resolve("import_test.json").toFile();
        Files.writeString(jsonFile.toPath(), json);

        List<Tour> importedTours = service.importToursFromJson(jsonFile);

        assertEquals(1, importedTours.size());
        Tour imported = importedTours.get(0);
        assertEquals("ImportedTour", imported.getName());
        assertEquals("import desc", imported.getDescription());
        assertEquals("importStart", imported.getStart());
        assertEquals("importDest", imported.getDestination());
        assertEquals("bike", imported.getTransportType());
        assertEquals(42.0, imported.getDistance());
        assertEquals(123.0, imported.getEstimatedTime());

        // Aufräumen
        Files.deleteIfExists(jsonFile.toPath());
        Files.deleteIfExists(tempDir);
    }
}
