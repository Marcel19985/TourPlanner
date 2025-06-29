package org.example.tourplanner.businessLayer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.example.tourplanner.helpers.UUIDv7Generator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity //Tabelle in db
@Table(name = "tours")
@JsonIgnoreProperties(ignoreUnknown = true) //Ignoriert unbekannte Eigenschaften bei der Deserialisierung von JSON
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

    @Column(name = "transport_type", nullable = false)
    private String transportType;

    @Column(nullable = false)
    private double distance;

    @Column(name = "estimated_time", nullable = false)
    private double estimatedTime;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy gibt an, dass die Tour-Log-Tabelle den Fremdschlüssel Tour enthält; cascade = CascadeType.ALL -> alle Operationen (z.B. persist, remove) werden auf die TourLogs angewendet; orphanRemoval = true -> wenn ein TourLog aus der Liste entfernt wird, wird es auch aus der Datenbank gelöscht; cascade = CascadeType.ALL -> Operationen auf Tour (z.b. persist, remove) werden auf TourLogs angewendet
    @JsonManagedReference //Verhindert zirkuläre Referenzen bei der Serialisierung: wichtig, da TourLogs in Tour enthalten sind und Tour in TourLog referenziert wird (verhindert StackOverflowError bei der Serialisierung)
    private List<TourLog> tourLogs = new ArrayList<>();

    @Transient
    public double getAverageRating() {
        return tourLogs.isEmpty()
                ? 0
                : tourLogs.stream()
                .mapToInt(TourLog::getRating)
                .average()
                .orElse(0);
    }

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

    }

    //Kein-Arg-Konstruktor für JPA
    public Tour() {}

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

    public Tour(String name, String description, String start, String destination, String transportType) { //Konstruktor für OpenRouteService
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.description = description;
        this.start = start;
        this.destination = destination;
        this.transportType = transportType;
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


    public String getTourImagePath() {
        return "target/images/" + id + ".png";
    }

    public double getAvgTime() {
        return this.getTourLogs().stream().mapToDouble(TourLog::getTotalTime).average().orElse(0);
    }

    public double getAvgDistance() {
        return this.getTourLogs().stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0);
    }

    public double getAvgRating() {
        return this.getTourLogs().stream().mapToInt(TourLog::getRating).average().orElse(0);
    }

}
