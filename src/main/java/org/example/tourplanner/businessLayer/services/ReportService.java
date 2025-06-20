package org.example.tourplanner.businessLayer.services;

import com.itextpdf.layout.element.Text;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;

@Service
public class ReportService {


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
        document.add(new Paragraph(new Text("Distance: ").setBold()).add(String.format("%.2f km", tour.getDistance())));
        document.add(new Paragraph(new Text("Estimated Time: ").setBold()).add(String.format("%.0f min", tour.getEstimatedTime())));
        if(tour.getPopularity() == "not rated yet") {
            document.add(new Paragraph(new Text("Popularity: ").setBold()).add("Not rated yet"));
        } else {
            document.add(new Paragraph(new Text("Popularity: ").setBold()).add(tour.getPopularity() + "/10")); //hier gibt bereits der getter eine Nachkommastelle
        }
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
            image.setWidth(220);
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
                    .add(new Text("  Date: ").setBold()).add(log.getDate() + "\n")
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
                logImage.setWidth(220);
                logImage.setHeight(150);
                document.add(logImage);
            } else {
                document.add(new Paragraph("No image for this log."));
            }
            // Optional: Abstand zwischen Logs
            document.add(new Paragraph(" "));
        }

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
            double avgTime = tour.getAvgTime();
            double avgDistance = tour.getAvgDistance();
            double avgRating = tour.getAvgRating();

            // Tour-Infos
            document.add(new Paragraph(new Text("Tour: ").setBold()).add(tour.getName()));
            document.add(new Paragraph(new Text("Description: ").setBold()).add(tour.getDescription()));
            document.add(new Paragraph(new Text("Start: ").setBold()).add(tour.getStart()));
            document.add(new Paragraph(new Text("Destination: ").setBold()).add(tour.getDestination()));
            document.add(new Paragraph(new Text("Transport Type: ").setBold()).add(tour.getTransportType()));
            document.add(new Paragraph(new Text("Distance: ").setBold()).add(String.format("%.2f km", tour.getDistance())));
            document.add(new Paragraph(new Text("Estimated Time: ").setBold()).add(String.format("%.0f min", tour.getEstimatedTime())));

            // Durchschnittswerte
            Paragraph averages = new Paragraph()
                    .setMarginLeft(20) // Einrückung
                    .add(new Text("Average Time: ").setBold()).add(avgTime + " min\n")
                    .add(new Text("Average Distance: ").setBold()).add(avgDistance + " km\n")
                    .add(new Text("Average Rating: ").setBold()).add(avgRating + "/10\n");
            if(avgTime+avgDistance+avgRating == 0) {
                document.add(new Paragraph(new Text("No logs have been recorded for this tour.\n")));
            }
            else {
                document.add(averages);
            }

            // Bild hinzufügen
            String imagePath = "target/images/" + tour.getId() + ".png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(
                        com.itextpdf.io.image.ImageDataFactory.create(imagePath)
                );
                image.setWidth(220);
                image.setHeight(150);
                document.add(image);
            } else {
                document.add(new Paragraph("Image not available for this tour."));
            }

            document.add(new Paragraph("\n"));
        }

        document.close();
    }
}
