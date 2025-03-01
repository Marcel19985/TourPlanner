package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;

import java.util.function.Consumer;

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

    @FXML
    private void initialize() {
        // Werte für das Transportmittel hinzufügen
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
    }

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    @FXML
    private void onCreateButtonClick() {
        if (TourValidatorController.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {

            // Werte aus den Feldern auslesen
            String name = tourNameField.getText();
            String description = tourDescriptionField.getText();
            String start = startField.getText();
            String destination = destinationField.getText();
            String transportType = transportTypeBox.getValue().toString();

            // Neue Tour erstellen
            Tour newTour = new Tour(name, description, start, destination, transportType);

            // Callback-Funktion aufrufen, falls gesetzt
            if (onTourCreatedCallback != null) {
                onTourCreatedCallback.accept(newTour);
            }

            closeWindow();
        }
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
