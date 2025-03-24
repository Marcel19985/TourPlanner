package org.example.tourplanner.ui.viewmodels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.tourplanner.data.models.Tour;

public class TourViewModel {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty start = new SimpleStringProperty();
    private final StringProperty destination = new SimpleStringProperty();
    private final StringProperty transportType = new SimpleStringProperty();
    private final DoubleProperty distance = new SimpleDoubleProperty();
    private final DoubleProperty estimatedTime = new SimpleDoubleProperty();

    private final Tour tour;

    public TourViewModel(Tour tour) {
        this.tour = tour;
        this.name.set(tour.getName());
        this.description.set(tour.getDescription());
        this.start.set(tour.getStart());
        this.destination.set(tour.getDestination());
        this.transportType.set(tour.getTransportType());
        this.distance.set(tour.getDistance());
        this.estimatedTime.set(tour.getEstimatedTime());
    }

    public void updateTour() {
        tour.setName(name.get());
        tour.setDescription(description.get());
        tour.setStart(start.get());
        tour.setDestination(destination.get());
        tour.setTransportType(transportType.get());
        // Für distance und estimatedTime ist ggf. eine API-Abfrage nötig.
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty startProperty() { return start; }
    public StringProperty destinationProperty() { return destination; }
    public StringProperty transportTypeProperty() { return transportType; }
    public DoubleProperty distanceProperty() { return distance; }
    public DoubleProperty estimatedTimeProperty() { return estimatedTime; }

    public Tour getTour() { return tour; }
}
