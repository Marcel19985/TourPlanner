package org.example.tourplanner.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;
import org.example.tourplanner.ui.viewmodels.TourViewModel;

import java.io.IOException;

import static org.example.tourplanner.ui.controllers.TourValidatorController.showAlert;

public class TourViewController {

    private MainViewModel viewModel;

    public TourViewController() {

    }

    @FXML
    private GridPane tourDetailsPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label startLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label transportTypeLabel;

    @FXML
    private Label distanceLabel;

    @FXML
    private Label estimatedTimeLabel;

    private Tour currentTour;

    @FXML
    public void initialize() {

        tourDetailsPane.setVisible(false);
    }

    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setTour(Tour tour) {
        if (tour != null) {
            this.currentTour = tour;
            nameLabel.setText(tour.getName());
            descriptionLabel.setText(tour.getDescription());
            startLabel.setText(tour.getStart());
            destinationLabel.setText(tour.getDestination());
            transportTypeLabel.setText(tour.getTransportType());
            distanceLabel.setText(String.format("%.2f km", tour.getDistance()));
            estimatedTimeLabel.setText(String.format("%.2f min", tour.getEstimatedTime()));
            this.currentTour = tour;
            tourDetailsPane.setVisible(true);
        } else {
            tourDetailsPane.setVisible(false);
            this.currentTour = null;
        }
    }
}


