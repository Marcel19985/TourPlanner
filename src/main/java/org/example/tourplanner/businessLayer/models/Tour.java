package org.example.tourplanner.businessLayer.models;

import org.example.tourplanner.helpers.UUIDv7Generator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity //Tabelle in db
@Table(name = "tours")
public class Tour {

    @Id //Primärschlüssel
    @Column(name = "id", columnDefinition = "uuid") //Spaltentyp uuid
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1024)
    private String description;

    @Column(name = "start_location", nullable = false)
    private String start;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "transport_type", nullable = false) //ToDo: Enum
    private String transportType;

    @Column(nullable = false)
    private double distance;

    @Column(name = "estimated_time", nullable = false)
    private double estimatedTime;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy gibt an, dass die Tour-Log-Tabelle den Fremdschlüssel Tour enthält
    private List<TourLog> tourLogs = new ArrayList<>();

    @Transient //Transient wird von JPA ignoriert -> keine Spalte in Tabelle
    public String getPopularity() {
        if (tourLogs.isEmpty()) {
            return "not rated yet";
        }
        double averageRating = tourLogs.stream() //stream() gibt einen Stream von TourLogs zurück
                                       .mapToInt(TourLog::getRating) //wandelt TourLog in int
                                       .average()
                                       .orElse(0); //wenn kein Durchschnitt vorhanden ist, wird 0 zurückgegeben (Liste leer)
        return String.format("%.1f", averageRating);
    }

    @Transient
    public boolean isChildFriendly() {
        if (estimatedTime > 150) {
            return false;
        }
        if (tourLogs.isEmpty()) {
            return true; //Keine Logs, nur Zeit wird berücksichtigt
        }
        double averageRating = tourLogs.stream()
                                       .mapToInt(TourLog::getRating)
                                       .average()
                                       .orElse(0);
        return averageRating >= 6;
        //todo: maybe auch difficulty hinzufügen
    }

    //Kein-Arg-Konstruktor für JPA
    public Tour() {}

   /* public Tour(String name, String description, String start, String destination, String transportType) { //todo: delete weil vermutlich nicht verwendet wird
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.description = description;
        this.start = start;
        this.destination = destination;
        this.transportType = transportType;
    }*/

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

    // Getter & Setter

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
    public void setDistance(double distance) { this.distance = distance; }

    public double getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(double estimatedTime) { this.estimatedTime = estimatedTime; }

    public List<TourLog> getTourLogs() {
        return tourLogs;
    }

    public void addTourLog(TourLog tourLog) {
        tourLogs.add(tourLog);
        tourLog.setTour(this);
    }

    public void removeTourLog(TourLog tourLog) {
        tourLogs.remove(tourLog);
        tourLog.setTour(null);
    }

    public String getTourImagePath() {
        return "target/images/" + id + ".png";
    }

}
