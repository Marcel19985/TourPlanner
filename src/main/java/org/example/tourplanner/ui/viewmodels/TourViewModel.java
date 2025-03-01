package org.example.tourplanner.ui.viewmodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.tourplanner.data.models.Tour;

public class TourViewModel {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty start = new SimpleStringProperty();
    private final StringProperty destination = new SimpleStringProperty();
    private final StringProperty transportType = new SimpleStringProperty();
    private final Tour tour;

    public TourViewModel(Tour tour) {
        this.tour = tour;
        this.name.set(tour.getName());
        this.description.set(tour.getDescription());
        this.start.set(tour.getStart());
        this.destination.set(tour.getDestination());
        this.transportType.set(tour.getTransportType());
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty startProperty() { return start; }
    public StringProperty destinationProperty() { return destination; }
    public StringProperty transportTypeProperty() { return transportType; }

    public void updateTour() {
        tour.setName(name.get());
        tour.setDescription(description.get());
        tour.setStart(start.get());
        tour.setDestination(destination.get());
        tour.setTransportType(transportType.get());
    }
}
