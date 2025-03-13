package org.example.tourplanner.ui.viewmodels;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tourplanner.data.models.TourLog;

import java.time.LocalDate;

public class TourLogViewModel {

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty tourName = new SimpleStringProperty();
    private final StringProperty difficulty = new SimpleStringProperty();
    private final DoubleProperty totalDistance = new SimpleDoubleProperty();
    private final DoubleProperty totalTime = new SimpleDoubleProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final TourLog tourLog;

    // ObservableList für alle TourLogs
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public TourLogViewModel(TourLog tourLog) {
        this.tourLog = tourLog;
        this.date.set(tourLog.getDate());
        this.name.set(tourLog.getName());
        this.tourName.set(tourLog.getTourName());
        this.comment.set(tourLog.getComment());
        this.difficulty.set(tourLog.getDifficulty());
        this.totalDistance.set(tourLog.getTotalDistance());
        this.totalTime.set(tourLog.getTotalTime());
        this.rating.set(tourLog.getRating());

        // Optional: Anfangs-TourLog zur Liste hinzufügen
        this.tourLogs.add(tourLog);
    }

    // Getter für die Properties
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty commentProperty() {
        return comment;
    }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty tourNameProperty() {
        return tourName;
    }

    public StringProperty difficultyProperty() {
        return difficulty;
    }

    public DoubleProperty totalDistanceProperty() {
        return totalDistance;
    }

    public DoubleProperty totalTimeProperty() {
        return totalTime;
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    // Getter für die Liste der TourLogs
    public ObservableList<TourLog> getTourLogs() {
        return tourLogs;
    }

    // Methode zum Hinzufügen eines TourLogs zur Liste
    public void addTourLog(TourLog tourLog) {
        tourLogs.add(tourLog);
    }


    // Methode zum Aktualisieren des TourLogs
    public void updateTourLog() {
        tourLog.setName(name.get());
        tourLog.setTourName(tourName.get());
        tourLog.setDate(date.get());
        tourLog.setComment(comment.get());
        tourLog.setDifficulty(difficulty.get());
        tourLog.setTotalDistance(totalDistance.get());
        tourLog.setTotalTime(totalTime.get());
        tourLog.setRating(rating.get());
    }


    // Optional: Methode zum Erstellen eines neuen TourLogs aus den aktuellen Werten
    public TourLog createTourLog() {
        return new TourLog(name.get(), date.get(), comment.get(), difficulty.get(), totalDistance.get(), totalTime.get(), rating.get());
    }
}
