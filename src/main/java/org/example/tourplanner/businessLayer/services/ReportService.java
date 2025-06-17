package org.example.tourplanner.businessLayer.services;

import com.itextpdf.layout.element.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);


    public void generateTourReportPdf(Tour tour, String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Überschrift
        Text title = new Text("Tour Report\n").setBold().setFontSize(16);
        document.add(new Paragraph(title));
        document.add(new Paragraph("=============\n"));

        // Tour-Infos
        document.add(new Paragraph(new Text("Name: ").setBold()).add(tour.getName()));
        document.add(new Paragraph(new Text("Description: ").setBold()).add(tour.getDescription()));
        document.add(new Paragraph(new Text("Start: ").setBold()).add(tour.getStart()));
        document.add(new Paragraph(new Text("Destination: ").setBold()).add(tour.getDestination()));
        document.add(new Paragraph(new Text("Transport Type: ").setBold()).add(tour.getTransportType()));
        document.add(new Paragraph(new Text("Distance: ").setBold()).add(tour.getDistance() + " km"));
        document.add(new Paragraph(new Text("Estimated Time: ").setBold()).add(tour.getEstimatedTime() + " min"));
        document.add(new Paragraph(new Text("Popularity: ").setBold()).add(tour.getPopularity()));
        document.add(new Paragraph(new Text("Child Friendly: ").setBold())
                .add(tour.isChildFriendly() ? "Yes" : "No"));
        document.add(new Paragraph("\n"));

        // Bild hinzufügen
        String imagePath = "target/images/" + tour.getId() + ".png";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(imagePath)
            );
            image.setWidth(200);
            image.setHeight(150);
            document.add(image);
        } else {
            document.add(new Paragraph("Image not available for this tour."));
        }
        document.add(new Paragraph("\n"));

        // Tour-Logs
        Text logsTitle = new Text("Tour Logs:\n").setBold().setFontSize(14);
        document.add(new Paragraph(logsTitle));

        for (TourLog log : tour.getTourLogs()) {
            Paragraph logParagraph = new Paragraph()
                    .setMarginLeft(20) // Einrückung
                    .add(new Text("- Date: ").setBold()).add(log.getDate() + "\n")
                    .add(new Text("  Name: ").setBold()).add(log.getName() + "\n")
                    .add(new Text("  Comment: ").setBold()).add(log.getComment() + "\n")
                    .add(new Text("  Difficulty: ").setBold()).add(log.getDifficulty() + "\n")
                    .add(new Text("  Total Time: ").setBold()).add(log.getTotalTime() + " min\n")
                    .add(new Text("  Rating: ").setBold()).add(log.getRating() + "/10\n");
            document.add(logParagraph);

            // Bild für TourLog anzeigen, falls vorhanden
            String logImagePath = "target/images/logs/" + log.getId() + ".png";
            File logImageFile = new File(logImagePath);
            if (logImageFile.exists()) {
                // Bild für jeden Log neu laden und einfügen
                com.itextpdf.layout.element.Image logImage = new com.itextpdf.layout.element.Image(
                        com.itextpdf.io.image.ImageDataFactory.create(logImageFile.getAbsolutePath())
                );
                logImage.setWidth(200);
                logImage.setHeight(150);
                document.add(logImage);
            } else {
                document.add(new Paragraph("No image for this log."));
            }
            // Optional: Abstand zwischen Logs
            document.add(new Paragraph(" "));
        }
        logger.info("Tour report generated: {}", filePath);

        document.close();
    }

    public void generateSummaryReportPdf(List<Tour> tours, String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Überschrift
        Text title = new Text("Summary Report\n").setBold().setFontSize(16);
        document.add(new Paragraph(title));
        document.add(new Paragraph("=============\n"));

        // Entferne Duplikate basierend auf der Tour-ID
        List<Tour> uniqueTours = tours.stream()
                .distinct()
                .toList();

        for (Tour tour : uniqueTours) {
            double avgTime = tour.getTourLogs().stream().mapToDouble(TourLog::getTotalTime).average().orElse(0);
            double avgDistance = tour.getTourLogs().stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0);
            double avgRating = tour.getTourLogs().stream().mapToInt(TourLog::getRating).average().orElse(0);

            // Tour-Infos
            document.add(new Paragraph(new Text("Tour: ").setBold()).add(tour.getName()));
            document.add(new Paragraph(new Text("Description: ").setBold()).add(tour.getDescription()));
            document.add(new Paragraph(new Text("Start: ").setBold()).add(tour.getStart()));
            document.add(new Paragraph(new Text("Destination: ").setBold()).add(tour.getDestination()));
            document.add(new Paragraph(new Text("Transport Type: ").setBold()).add(tour.getTransportType()));
            document.add(new Paragraph(new Text("Distance: ").setBold()).add(tour.getDistance() + " km"));

            // Durchschnittswerte
            Paragraph averages = new Paragraph()
                    .setMarginLeft(20) // Einrückung
                    .add(new Text("Average Time: ").setBold()).add(avgTime + " min\n")
                    .add(new Text("Average Distance: ").setBold()).add(avgDistance + " km\n")
                    .add(new Text("Average Rating: ").setBold()).add(avgRating + "/10\n");
            document.add(averages);

            // Bild hinzufügen
            String imagePath = "target/images/" + tour.getId() + ".png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(
                        com.itextpdf.io.image.ImageDataFactory.create(imagePath)
                );
                image.setWidth(200);
                image.setHeight(150);
                document.add(image);
            } else {
                document.add(new Paragraph("Image not available for this tour."));
            }

            document.add(new Paragraph("\n"));
        }
        logger.info("Summary report generated: {}", filePath);

        document.close();
    }
}
