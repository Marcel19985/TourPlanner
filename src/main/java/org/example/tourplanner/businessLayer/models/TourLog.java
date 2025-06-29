package org.example.tourplanner.businessLayer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.example.tourplanner.helpers.UUIDv7Generator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "tour_logs")
public class TourLog {

    @Id //Primärschlüssel
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) //layzy fetching -> Tour wird nur geladen, wenn es benötigt wird (spart ressourcen)
    @JoinColumn(name = "tour_id", nullable = false) //erzeugt eine Spalte tour_id in der TourLog-Tabelle, die auf die Tour-Tabelle verweist (Fremdschlüssel
    @JsonBackReference //Verhindert zirkuläre Referenzen bei der Serialisierung: wichtig, da TourLogs in Tour enthalten sind und Tour in TourLog referenziert wird (verhindert StackOverflowError bei der Serialisierung)
    private Tour tour;

    @Column(name = "log_date", nullable = false)
    private LocalDate date;

    @Column(name = "log_time", nullable = false)
    private LocalTime time;

    @Column(nullable = false, length = 1024)
    private String comment;

    @Column(nullable = false)
    private String difficulty;

    @Column(name = "total_distance", nullable = false)
    private double totalDistance;

    @Column(name = "total_time", nullable = false)
    private double totalTime;

    @Column(nullable = false)
    private int rating;

    //Kein-Arg-Konstruktor für JPA
    public TourLog() {}

    public TourLog(String name, LocalDate date, LocalTime time, String comment, String difficulty, double totalDistance, double totalTime, int rating) {
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.date = date;
        this.time = time;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    // Getter & Setter

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getTotalTime() { return totalTime; }
    public void setTotalTime(double totalTime) { this.totalTime = totalTime; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }


}


