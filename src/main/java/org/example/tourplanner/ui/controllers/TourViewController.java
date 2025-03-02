package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.tourplanner.data.models.Tour;

public class TourViewController {

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

    @FXML
    public void initialize() {
        // Standardmäßig ist das Detail-Pane unsichtbar, bis eine Tour gesetzt wird.
        tourDetailsPane.setVisible(false);
    }

    public void setTour(Tour tour) {
        if (tour != null) {
            nameLabel.setText(tour.getName());
            descriptionLabel.setText(tour.getDescription());
            startLabel.setText(tour.getStart());
            destinationLabel.setText(tour.getDestination());
            transportTypeLabel.setText(tour.getTransportType());
            distanceLabel.setText(String.format("%.2f km", tour.getDistance()));
            estimatedTimeLabel.setText(String.format("%.2f min", tour.getEstimatedTime()));

            tourDetailsPane.setVisible(true);
        } else {
            tourDetailsPane.setVisible(false);
        }
    }
}
