package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.tourplanner.data.models.Tour;

public class TourViewController {

    @FXML
    private GridPane tourDetailsPane; // Referenz zum GridPane

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    public void initialize() {
        // Startet unsichtbar, bis eine Tour ausgewählt wird
        tourDetailsPane.setVisible(false);
    }

    public void setTour(Tour tour) {
        if (tour != null) {
            nameLabel.setText(tour.getName());
            descriptionLabel.setText(tour.getDescription());

            // Zeige das GridPane an
            tourDetailsPane.setVisible(true);
        } else {
            // Verstecke das GridPane, wenn keine Tour ausgewählt ist
            tourDetailsPane.setVisible(false);
        }
    }
}
