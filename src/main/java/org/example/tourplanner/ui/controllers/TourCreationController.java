package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.TourViewModel;
import java.util.function.Consumer;

public class TourCreationController {

    @FXML private TextField tourNameField;
    @FXML private TextField tourDescriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;

    private Consumer<Tour> onTourCreatedCallback;
    private Consumer<Tour> onTourUpdatedCallback;

    // Hier speichern wir das Original-ViewModel und den Editing-Clone separat.
    private TourViewModel originalTourViewModel = null;
    private TourViewModel editingTourViewModel = null;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        //delete: wird verwendet um schneller auszufüllen
        tourNameField.setText("Test Tour");
        tourDescriptionField.setText("Test Beschreibung");
        startField.setText("Wien");
        destinationField.setText("Linz");
        transportTypeBox.setValue("Car");
    }

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    public void setOnTourUpdatedCallback(Consumer<Tour> callback) {
        this.onTourUpdatedCallback = callback;
    }

    /**
     * Wird im Bearbeitungsmodus aufgerufen.
     * Es wird ein Editing-Clone des übergebenen TourViewModels erstellt,
     * an den dann die UI-Felder gebunden werden.
     */
    public void setTourForEditing(TourViewModel original) {
        this.originalTourViewModel = original;
        this.editingTourViewModel = new TourViewModel(original); // Editing-Clone erstellen

        // Bidirektionales Binding an den Editing-Clone
        tourNameField.textProperty().bindBidirectional(editingTourViewModel.nameProperty());
        tourDescriptionField.textProperty().bindBidirectional(editingTourViewModel.descriptionProperty());
        startField.textProperty().bindBidirectional(editingTourViewModel.startProperty());
        destinationField.textProperty().bindBidirectional(editingTourViewModel.destinationProperty());
        transportTypeBox.valueProperty().bindBidirectional(editingTourViewModel.transportTypeProperty());

        // Nur Name und Description sollen editierbar sein:
        startField.setDisable(true);
        destinationField.setDisable(true);
        transportTypeBox.setDisable(true);

        // Falls du Felder für distance/estimatedTime hast, diese ebenfalls deaktivieren:
        // distanceField.setDisable(true);
        // estimatedTimeField.setDisable(true);
    }

    @FXML
    private void onSaveButtonClick() {
        // Überprüfe, ob die Eingaben gültig sind
        if (!InputValidator.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        if (editingTourViewModel != null) {
            // Bearbeitungsmodus: Änderungen vom Editing-Clone in das Original übertragen
            originalTourViewModel.copyFrom(editingTourViewModel);
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(originalTourViewModel.getTour());
            }
        } else {
            // Erstellungsmodus: Vor Erzeugung der neuen Tour werden die Routendetails abgerufen.
            try {
                String start = startField.getText();
                String destination = destinationField.getText();
                String transportType = transportTypeBox.getValue();
                // Abruf der Routendetails via OpenRouteServiceClient
                double[] routeDetails = OpenRouteServiceClient.getRouteDetails(start, destination, transportType);
                double distance = routeDetails[0];
                double estimatedTime = routeDetails[1];

                // Erzeuge die neue Tour mit den berechneten Werten
                Tour newTour = new Tour(
                        tourNameField.getText(),
                        tourDescriptionField.getText(),
                        start,
                        destination,
                        transportType,
                        distance,
                        estimatedTime
                );
                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(newTour);
                }
            } catch (Exception e) {
                showAlert("An error occurred while retrieving route information: " + e.getMessage());
                return;
            }
        }
        closeWindow();
    }

    @FXML
    private void onCancelButtonClick() {
        // Beim Cancel wird der Editing-Clone verworfen und das Original bleibt unverändert.
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tourNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
