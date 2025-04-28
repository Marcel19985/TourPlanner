package org.example.tourplanner.businessLayer.services;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    public void generateTourReport(Tour tour, String filePath) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Tour Report\n");
            writer.write("===========\n");
            writer.write("Name: " + tour.getName() + "\n");
            writer.write("Description: " + tour.getDescription() + "\n");
            writer.write("Start: " + tour.getStart() + "\n");
            writer.write("Destination: " + tour.getDestination() + "\n");
            writer.write("Transport Type: " + tour.getTransportType() + "\n");
            writer.write("Distance: " + tour.getDistance() + " km\n");
            writer.write("Estimated Time: " + tour.getEstimatedTime() + " min\n");
            writer.write("Popularity: " + tour.getPopularity() + "\n");
            writer.write("Child Friendly: " + (tour.isChildFriendly() ? "Yes" : "No") + "\n\n");

            writer.write("Tour Logs:\n");
            for (TourLog log : tour.getTourLogs()) {
                writer.write("- Date: " + log.getDate() + "\n");
                writer.write("  Comment: " + log.getComment() + "\n");
                writer.write("  Difficulty: " + log.getDifficulty() + "\n");
                writer.write("  Total Time: " + log.getTotalTime() + " min\n");
                writer.write("  Rating: " + log.getRating() + "/10\n\n");
            }
        }
    }

    public void generateSummaryReport(List<Tour> tours, String filePath) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Summary Report\n");
            writer.write("==============\n");

            // Verarbeite jede Tour nur einmal
            for (Tour tour : tours.stream().distinct().toList()) {
                double avgTime = tour.getTourLogs().stream().mapToDouble(TourLog::getTotalTime).average().orElse(0);
                double avgDistance = tour.getTourLogs().stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0);
                double avgRating = tour.getTourLogs().stream().mapToInt(TourLog::getRating).average().orElse(0);

                writer.write("Tour: " + tour.getName() + "\n");
                writer.write("  Average Time: " + avgTime + " min\n");
                writer.write("  Average Distance: " + avgDistance + " km\n");
                writer.write("  Average Rating: " + avgRating + "/10\n\n");
            }
        }
    }
}