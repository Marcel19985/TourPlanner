package org.example.tourplanner.businessLayer.services;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;
    private Tour sampleTour;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
        sampleTour = new Tour("TestTour", "desc", "start", "dest", "car", 10, 20);

        TourLog log = new TourLog(
                "Log1",
                LocalDate.now(),
                LocalTime.now(),
                "Kommentar",
                "easy",
                10.0,
                20.0,
                8
        );
        sampleTour.addTourLog(log);
    }

    @Test
    void testGenerateTourReportPdfCreatesFile() throws IOException {
        String filePath = "target/test-report-tour.pdf";
        File file = new File(filePath);
        if (file.exists()) file.delete();

        reportService.generateTourReportPdf(sampleTour, filePath);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        // Aufräumen
        file.delete();
    }

    @Test
    void testGenerateSummaryReportPdfCreatesFile() throws IOException {
        String filePath = "target/test-report-summary.pdf";
        File file = new File(filePath);
        if (file.exists()) file.delete();

        reportService.generateSummaryReportPdf(List.of(sampleTour), filePath);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        // Aufräumen
        file.delete();
    }
}
