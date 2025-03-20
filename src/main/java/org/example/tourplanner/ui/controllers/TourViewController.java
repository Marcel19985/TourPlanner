package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.MainViewModel;

import java.net.URL;

public class TourViewController {

    private MainViewModel viewModel;

    public TourViewController() {

    }

    @FXML private GridPane tourDetailsPane;

    @FXML private Label nameLabel;

    @FXML private Label descriptionLabel;

    @FXML private Label startLabel;

    @FXML private Label destinationLabel;

    @FXML private Label transportTypeLabel;

    @FXML private Label distanceLabel;

    @FXML private Label estimatedTimeLabel;

    @FXML private ImageView tourImageView;

    private Tour currentTour;

    @FXML
    public void initialize() {

        tourDetailsPane.setVisible(false);
        String imagePath = "/images/Placeholder.png";
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl == null) {
            System.err.println("Bild nicht gefunden! Erwarteter Pfad: " + imagePath);
        } else {
            Image image = new Image(imageUrl.toExternalForm());
            tourImageView.setImage(image);
        }
    }
    // todo: Platzhalter für bild hinzufügen DONE
    // todo: Bild dynamischer machen
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


