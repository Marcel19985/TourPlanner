package org.example.tourplanner.data.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tourplanner.helpers.UUIDv7Generator;

import java.util.UUID;

public class Tour {
    private final UUID id; //soll als primary key für Datenbank und als Filename für Map- Images verwendet werden
    private String name;
    private String description;
    private String start;
    private String destination;
    private String transportType; //eventuell enum verwenden
    private double distance;
    private double estimatedTime;
    private String tourImagePath;

    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList(); //man kann Listener definieren, die direkt bei einer Änderung einer observable list benachrichtigt werden

    public ObservableList<TourLog> getTourLogs() {
        return tourLogs;
    }

    public void addTourLog(TourLog tourLog) {
        tourLogs.add(tourLog);
    }

    public Tour(String name, String description, String start, String destination, String transportType) {
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.description = description;
        this.start = start;
        this.destination = destination;
        this.transportType = transportType;
    }

    public Tour(String name, String description, String start, String destination, String transportType, double distance, double estimatedTime) {
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.description = description;
        this.start = start;
        this.destination = destination;
        this.transportType = transportType;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }

    @Override
    public String toString() {
        return name + " - " + start + " → " + destination + " (" + transportType + ")";
    }

    //Getter & Setter:

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public double getDistance() { return distance; }
    public double getEstimatedTime() { return estimatedTime; }

    public String getTourImagePath() { return "/images/" + this.id + ".png"; }
    public void setImagePath(String tourImagePath) { this.tourImagePath = tourImagePath; }

}

