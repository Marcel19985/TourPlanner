package org.example.tourplanner.presentationLayer.viewmodels;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;

public class TourViewModel {
    private final Tour tour;
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty start = new SimpleStringProperty();
    private final StringProperty destination = new SimpleStringProperty();
    private final StringProperty transportType = new SimpleStringProperty();
    private final DoubleProperty distance = new SimpleDoubleProperty();
    private final DoubleProperty estimatedTime = new SimpleDoubleProperty();
    private final StringProperty popularity = new SimpleStringProperty();
    private final BooleanProperty childFriendly = new SimpleBooleanProperty();

    // Neue ObservableList für die TourLogViewModels
    private final ObservableList<TourLogViewModel> tourLogViewModels = FXCollections.observableArrayList();

    // Konstruktor: Initialisiert Properties und erstellt aus den TourLogs
    public TourViewModel(Tour tour) {
        this.tour = tour;
        this.name.set(tour.getName());
        this.description.set(tour.getDescription());
        this.start.set(tour.getStart());
        this.destination.set(tour.getDestination());
        this.transportType.set(tour.getTransportType());
        this.distance.set(tour.getDistance());
        this.estimatedTime.set(tour.getEstimatedTime());
        this.popularity.set(tour.getPopularity());
        this.childFriendly.set(tour.isChildFriendly());
        // Bestehende TourLogs in ViewModels umwandeln
        for (TourLog log : tour.getTourLogs()) {
            tourLogViewModels.add(new TourLogViewModel(log));
        }
    }

    // Kopierkonstruktor für Editing-Clones (bereits bekannt)
    public TourViewModel(TourViewModel other) {
        this.tour = other.tour;
        this.name.set(other.name.get());
        this.description.set(other.description.get());
        this.start.set(other.start.get());
        this.destination.set(other.destination.get());
        this.transportType.set(other.transportType.get());
        this.distance.set(other.distance.get());
        this.estimatedTime.set(other.estimatedTime.get());
        this.popularity.set(other.popularity.get());
    }

    // Methode, um Änderungen aus einem Editing-Clone ins Original zu kopieren
    public void copyFrom(TourViewModel editingClone) {
        this.name.set(editingClone.name.get());
        this.description.set(editingClone.description.get());
        this.start.set(editingClone.start.get());
        this.destination.set(editingClone.destination.get());
        this.transportType.set(editingClone.transportType.get());
        this.distance.set(editingClone.distance.get());
        this.estimatedTime.set(editingClone.estimatedTime.get());
        this.popularity.set(editingClone.popularity.get());

        // Aktualisiere auch das zugrunde liegende Tour-Datenmodell
        tour.setName(editingClone.name.get());
        tour.setDescription(editingClone.description.get());
        tour.setStart(editingClone.start.get());
        tour.setDestination(editingClone.destination.get());
        tour.setTransportType(editingClone.transportType.get());
        // distance und estimatedTime können ebenfalls aktualisiert werden, falls erforderlich
    }

    // Neue Methode: Gibt die ObservableList der TourLogViewModels zurück
    public ObservableList<TourLogViewModel> getTourLogViewModels() {
        return tourLogViewModels;
    }

    public void updateTour() {
        tour.setName(name.get());
        tour.setDescription(description.get());
        tour.setStart(start.get());
        tour.setDestination(destination.get());
        tour.setTransportType(transportType.get());
        // Falls distance/estimatedTime geändert werden sollen:
        // tour.setDistance(distance.get());
        // tour.setEstimatedTime(estimatedTime.get());
        this.popularity.set(tour.getPopularity());
        this.childFriendly.set(tour.isChildFriendly());
    }

    // Neue Methode: Fügt einen TourLog hinzu – sowohl im zugrunde liegenden Tour-Modell als auch in der ObservableList
    public void addTourLog(TourLog newLog) {
        tour.addTourLog(newLog); // Aktualisiert das Tour-Datenmodell
        tourLogViewModels.add(new TourLogViewModel(newLog)); // Fügt ein neues ViewModel hinzu
    }

    public Tour getTour() {
        return tour;
    }

    // Property-Methoden für das Data Binding
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    public StringProperty startProperty() {
        return start;
    }
    public StringProperty destinationProperty() {
        return destination;
    }
    public StringProperty transportTypeProperty() {
        return transportType;
    }
    public DoubleProperty distanceProperty() {
        return distance;
    }
    public DoubleProperty estimatedTimeProperty() {
        return estimatedTime;
    }
    public StringProperty popularityProperty() {
        return popularity;
    }
    public BooleanProperty childFriendlyProperty() {
        return childFriendly;
    }
}
