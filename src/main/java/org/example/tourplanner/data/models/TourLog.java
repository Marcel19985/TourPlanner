package org.example.tourplanner.data.models;

import org.example.tourplanner.helpers.UUIDv7Generator;

import java.time.LocalDate;
import java.util.UUID;

public class TourLog {

    private final UUID id; //soll als primary key für Datenbank und als Filename für Map- Images verwendet werden
    private String name;
    private String tourName;
    private LocalDate date;
    private String comment;
    private String difficulty;
    private double totalDistance;
    private double totalTime;
    private int rating;

    // Konstruktor
    public TourLog(String name, LocalDate date, String comment, String difficulty, double totalDistance, double totalTime, int rating) {
        this.id = UUIDv7Generator.generateUUIDv7();
        this.name = name;
        this.date = date;
        this.comment = comment;
        this.difficulty = difficulty;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.rating = rating;
    }

    // Getter und Setter

    public UUID getId() { return id; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalTime() {
        return totalTime;
    }
    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return name;
    }
}


