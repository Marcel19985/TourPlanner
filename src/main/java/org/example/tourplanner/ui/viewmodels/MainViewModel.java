package org.example.tourplanner.ui.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;

public class MainViewModel {

    private final ObservableList<Tour> tours = FXCollections.observableArrayList();
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList(); // Liste für TourLogs

    // Getter für die Liste der Touren
    public ObservableList<Tour> getTours() {
        return tours;
    }

    // Getter für die Liste der TourLogs
    public ObservableList<TourLog> getTourLogs() {
        return tourLogs;
    }

    // Methode, um eine Tour hinzuzufügen
    public void addTour(String name, String description, String start, String destination, String transportType) {
        tours.add(new Tour(name, description, start, destination, transportType));
    }

    // Methode, um ein TourLog zu einer bestimmten Tour hinzuzufügen
    public void addTourLog(TourLog tourLog) {
        tourLogs.add(tourLog);
    }

    // Methode, um TourLogs für eine bestimmte Tour zu setzen
    public void setTourLogsForTour(Tour tour) {
        // Diese Methode kann an deine Tour-Logik angepasst werden, um TourLogs für eine ausgewählte Tour zu setzen
        tourLogs.setAll(tour.getTourLogs());
    }

}
