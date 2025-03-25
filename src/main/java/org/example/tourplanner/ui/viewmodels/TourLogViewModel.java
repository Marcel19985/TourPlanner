package org.example.tourplanner.ui.viewmodels;

import javafx.beans.property.*;
import org.example.tourplanner.data.models.TourLog;
import java.time.LocalDate;
import java.time.LocalTime;

public class TourLogViewModel {

    private final TourLog tourLog;
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> time;
    private final StringProperty comment;
    private final StringProperty name;
    private final StringProperty tourName;
    private final StringProperty difficulty;
    private final DoubleProperty totalDistance;
    private final DoubleProperty totalTime;
    private final IntegerProperty rating;

    public TourLogViewModel(TourLog tourLog) {
        this.tourLog = tourLog;
        this.date = new SimpleObjectProperty<>(tourLog.getDate());
        this.time = new SimpleObjectProperty<>(tourLog.getTime());
        this.comment = new SimpleStringProperty(tourLog.getComment());
        this.name = new SimpleStringProperty(tourLog.getName());
        this.tourName = new SimpleStringProperty(tourLog.getTourName());
        this.difficulty = new SimpleStringProperty(tourLog.getDifficulty());
        this.totalDistance = new SimpleDoubleProperty(tourLog.getTotalDistance());
        this.totalTime = new SimpleDoubleProperty(tourLog.getTotalTime());
        this.rating = new SimpleIntegerProperty(tourLog.getRating());
    }

    // Kopierkonstruktor: Erzeugt einen Editing-Clone
    public TourLogViewModel(TourLogViewModel other) {
        this.tourLog = other.tourLog; // Gleiche zugrunde liegende Instanz
        this.date = new SimpleObjectProperty<>(other.date.get());
        this.time = new SimpleObjectProperty<>(other.time.get());
        this.comment = new SimpleStringProperty(other.comment.get());
        this.name = new SimpleStringProperty(other.name.get());
        this.tourName = new SimpleStringProperty(other.tourName.get());
        this.difficulty = new SimpleStringProperty(other.difficulty.get());
        this.totalDistance = new SimpleDoubleProperty(other.totalDistance.get());
        this.totalTime = new SimpleDoubleProperty(other.totalTime.get());
        this.rating = new SimpleIntegerProperty(other.rating.get());
    }

    // Kopiert die Werte aus dem anderen ViewModel in dieses (z. B. beim Speichern)
    public void copyFrom(TourLogViewModel other) {
        this.name.set(other.name.get());
        this.comment.set(other.comment.get());
        this.tourName.set(other.tourName.get());
        this.date.set(other.date.get());
        this.time.set(other.time.get());
        this.difficulty.set(other.difficulty.get());
        this.totalDistance.set(other.totalDistance.get());
        this.totalTime.set(other.totalTime.get());
        this.rating.set(other.rating.get());
        updateTourLog();
    }

    // Überträgt die aktuellen Property-Werte in das zugrunde liegende TourLog-Objekt
    public void updateTourLog() {
        tourLog.setName(name.get());
        tourLog.setComment(comment.get());
        tourLog.setTourName(tourName.get());
        tourLog.setDate(date.get());
        tourLog.setTime(time.get());
        tourLog.setDifficulty(difficulty.get());
        tourLog.setTotalDistance(totalDistance.get());
        tourLog.setTotalTime(totalTime.get());
        tourLog.setRating(rating.get());
    }

    // Getter für den zugrunde liegenden TourLog
    public TourLog getTourLog() {
        return tourLog;
    }

    // Property-Methoden
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public ObjectProperty<LocalTime> timeProperty() { return time; }
    public StringProperty commentProperty() { return comment; }
    public StringProperty nameProperty() { return name; }
    public StringProperty tourNameProperty() { return tourName; }
    public StringProperty difficultyProperty() { return difficulty; }
    public DoubleProperty totalDistanceProperty() { return totalDistance; }
    public DoubleProperty totalTimeProperty() { return totalTime; }
    public IntegerProperty ratingProperty() { return rating; }
}
