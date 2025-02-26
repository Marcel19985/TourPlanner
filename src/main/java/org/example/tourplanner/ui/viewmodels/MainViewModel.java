package org.example.tourplanner.ui.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.tourplanner.data.models.Tour;

public class MainViewModel {

    private final ObservableList<Tour> tours = FXCollections.observableArrayList();

    public ObservableList<Tour> getTours() {
        return tours;
    }

    public void addTour(String name, String description) {
        tours.add(new Tour(name, description));
    }




}
