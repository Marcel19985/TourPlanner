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

    @FXML
    private void onCreateTourLog() {
        try {
            // Laden des FXML für das TourLog-Erstellungsfenster
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
            Parent root = loader.load();

            // Erstellen einer neuen Szene
            Scene scene = new Scene(root);

            // Erstellen eines neuen Stages (Fensters)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Create Tour Log");

            // Abrufen des Controllers des neuen Fensters
            TourLogCreationController controller = loader.getController();
            controller.setCurrentTour(currentTour);
            // Setzen von Callback und ViewModel, falls erforderlich
            controller.setOnTourLogCreatedCallback(tourLog -> viewModel.getTourLogs().add(tourLog));

            // Fenster anzeigen
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Fehler beim Laden des Fensters für das TourLog.");
        }
    }

    public void setTourLog(TourLog newTourLog) {
        nameLabel.setText(newTourLog.getComment());
    }
}




