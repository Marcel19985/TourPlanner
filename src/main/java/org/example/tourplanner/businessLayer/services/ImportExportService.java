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

@Service
public class ImportExportService {
    private static final Logger logger = LogManager.getLogger(ImportExportService.class);
    private static final String EXPORT_FOLDER = "import_export_json";
        public ImportExportService() {
            // Registriere das Modul für Java-Zeittypen
            mapper.registerModule(new JavaTimeModule());
            logger.info("Tours imported from JSON.");

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

        // Entferne Duplikate basierend auf der Tour-ID
        List<Tour> uniqueTours = tours.stream()
                .distinct()
                .toList();

        mapper.writeValue(file, uniqueTours);
        logger.info("Tours exported to JSON.");

    }

        public List<Tour> importToursFromJson(File jsonFile) throws IOException {
            return List.of(mapper.readValue(jsonFile, Tour[].class));
        }
    }

