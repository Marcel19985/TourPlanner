package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


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

    @FXML private ImageView mapImageView;

    private Tour currentTour;

    @FXML
    public void initialize() {
        tourDetailsPane.setVisible(false);

    }
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
            // Lade das Bild für das ImageView
            loadMapImage(tour);
            this.currentTour = tour;
            tourDetailsPane.setVisible(true);
        } else {
            tourDetailsPane.setVisible(false);
            this.currentTour = null;
        }
    }

    private void loadMapImage(Tour tour) {
        try {
            // Der Pfad zum gespeicherten Screenshot (z.B. target/images/{tourId}.png)
            File mapImageFile = new File("target/images/" + tour.getId() + ".png");

            if (mapImageFile.exists()) {
                // Erstelle ein Image aus der Datei
                Image mapImage = new Image(new FileInputStream(mapImageFile));

                // Setze das Bild in das ImageView
                mapImageView.setImage(mapImage);
            } else {
                System.err.println("Bilddatei nicht gefunden: " + mapImageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


