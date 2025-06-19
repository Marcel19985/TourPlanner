package org.example.tourplanner.presentationLayer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.presentationLayer.viewmodels.MainViewModel;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;

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

    @FXML private Label popularityLabel;

    @FXML private Label childFriendlyLabel;

    private Tour currentTour;

    private static final Logger logger = LogManager.getLogger(TourViewController.class);

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
            estimatedTimeLabel.setText(String.format("%.0f min", tour.getEstimatedTime()));

            // Binde die Popularität dynamisch an die Änderungen der TourLogs
            popularityLabel.textProperty().bind(Bindings.createStringBinding(
                tour::getPopularity,
                FXCollections.observableList(tour.getTourLogs()) //ObservableList wenn sich TourLogs ändern
            ));

            childFriendlyLabel.setText(tour.isChildFriendly() ? "\u2714" : "\u2718"); //✔ oder ✘
            // Lade das Bild für das ImageView
            loadMapImage(tour);
            tourDetailsPane.setVisible(true);
        } else {
            tourDetailsPane.setVisible(false);
            popularityLabel.textProperty().unbind(); // Unbind, wenn keine Tour ausgewählt ist
            popularityLabel.setText("");
            childFriendlyLabel.setText("");
            this.currentTour = null;
        }
    }

    private void loadMapImage(Tour tour) {
        // Der Pfad zum gespeicherten Screenshot (z.B. target/images/{tourId}.png)
        File mapImageFile = new File(tour.getTourImagePath());
        try {

            if (mapImageFile.exists()) {
                // Erstelle ein Image aus der Datei
                Image mapImage = new Image(new FileInputStream(mapImageFile));

                // Setze das Bild in das ImageView
                mapImageView.setImage(mapImage);
                logger.info("Loaded map image for tour {} from {}", tour.getId(), mapImageFile.getAbsolutePath());
            } else {
                System.err.println("Could not find image : " + mapImageFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Map image not found for tour {}: {}", tour.getId(), mapImageFile.getAbsolutePath());
        }
    }


}




