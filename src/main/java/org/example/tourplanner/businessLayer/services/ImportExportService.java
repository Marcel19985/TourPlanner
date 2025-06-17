package org.example.tourplanner.businessLayer.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportExportService {
    private static final Logger logger = LogManager.getLogger(ImportExportService.class);
    private static final String EXPORT_FOLDER = "import_export_json";
    public ImportExportService() {
        // Registriere das Modul für Java-Zeittypen
        mapper.registerModule(new JavaTimeModule());
        logger.info("ImportExportService initialized.");

    }

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 Format verwenden

    public void exportToursToJson(List<Tour> tours) throws IOException {
        Files.createDirectories(Paths.get(EXPORT_FOLDER));
        int fileNumber = 1;
        File file;
        do {
            file = new File(EXPORT_FOLDER + "/tours_export_" + fileNumber + ".json");
            fileNumber++;
        } while (file.exists());

        // Optional: Duplikate anhand von Name+Start+Destination+TransportType entfernen
        List<Tour> uniqueTours = tours.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                t -> t.getName() + "|" + t.getStart() + "|" + t.getDestination() + "|" + t.getTransportType(),
                                t -> t,
                                (t1, t2) -> t1
                        ),
                        m -> m.values().stream().toList()
                ));

        mapper.writeValue(file, uniqueTours);
        logger.info("Tours exported to JSON.");

    }

    public List<Tour> importToursFromJson(File jsonFile) throws IOException {
        logger.info("Tours imported from JSON.");
        return List.of(mapper.readValue(jsonFile, Tour[].class));
    }
}
