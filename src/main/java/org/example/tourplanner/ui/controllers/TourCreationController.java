package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.controllers.InputValidator;
import org.json.JSONException;
import java.io.IOException;
import java.util.function.Consumer;

import static org.example.tourplanner.ui.controllers.InputValidator.showAlert;

public class TourCreationController {

    @FXML
    private TextField tourNameField;

    @FXML
    private TextField tourDescriptionField;

    @FXML
    private TextField startField;

    @FXML
    private TextField destinationField;

    @FXML
    private ComboBox<String> transportTypeBox;


    private Consumer<Tour> onTourCreatedCallback;
    private Consumer<Tour> onTourUpdatedCallback;

    private Tour editingTour = null;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
    }

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    public void setOnTourUpdatedCallback(Consumer<Tour> callback) {
        this.onTourUpdatedCallback = callback;
    }

    public void setTourForEditing(Tour tour) {
        this.editingTour = tour;
        tourNameField.setText(tour.getName());
        tourDescriptionField.setText(tour.getDescription());
        startField.setText(tour.getStart());
        destinationField.setText(tour.getDestination());
        transportTypeBox.setValue(tour.getTransportType());

        // Nicht bearbeitbare Felder deaktivieren
        startField.setDisable(true);
        destinationField.setDisable(true);
        transportTypeBox.setDisable(true);
    }

    @FXML
    void onSaveButtonClick() {
        if (!InputValidator.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();

        if (editingTour != null) {
            // Bearbeitungsmodus: Nur Name & Beschreibung ändern
            editingTour.setName(name);
            editingTour.setDescription(description);

            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(editingTour);
            }
        } else {
            // Erstellungsmodus: Neue Tour erstellen (inkl. API-Abfrage)
            try {
                String start = startField.getText();
                String destination = destinationField.getText();
                String transportType = transportTypeBox.getValue();

                double[] routeDetails = OpenRouteServiceClient.getRouteDetails(start, destination, transportType);
                double distance = routeDetails[0];
                double estimatedTime = routeDetails[1];

                Tour newTour = new Tour(name, description, start, destination, transportType, distance, estimatedTime);
                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(newTour);
                }
            } catch (IOException | JSONException e) {
                showAlert("Fehler beim Abrufen der Route: " + e.getMessage());
                return;
            }
        }
        closeWindow();
    }

    @FXML
    private void onCancelButtonClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tourNameField.getScene().getWindow();
        stage.close();
    }
}
