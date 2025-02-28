package org.example.tourplanner.ui.viewmodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.tourplanner.data.models.Tour;

public class TourViewModel {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final Tour tour;

    public TourViewModel(Tour tour) {
        this.tour = tour;
        this.name.set(tour.getName());
        this.description.set(tour.getDescription());
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void updateTour() {
        tour.setName(name.get());
        tour.setDescription(description.get());
    }
}