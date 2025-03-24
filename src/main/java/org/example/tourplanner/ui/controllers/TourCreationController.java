package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.helpers.LocationNotFoundException;
import org.example.tourplanner.ui.controllers.InputValidator;
import org.example.tourplanner.ui.viewmodels.TourViewModel;
import org.json.JSONException;
import java.io.IOException;
import java.util.function.Consumer;

import static org.example.tourplanner.ui.controllers.InputValidator.showAlert;

public class TourCreationController {

    @FXML private TextField tourNameField;
    @FXML private TextField tourDescriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;
    //Read-only fields for displaying calculated values
    @FXML private TextField distanceField;
    @FXML private TextField estimatedTimeField;

    private Consumer<Tour> onTourCreatedCallback;
    private Consumer<Tour> onTourUpdatedCallback;

    private TourViewModel tourViewModel = null;

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

    public void setTourForEditing(TourViewModel tvm) {
        this.tourViewModel = tvm;
        // Bidirektionales Binding der Textfelder an die ViewModel Properties
        tourNameField.textProperty().bindBidirectional(tourViewModel.nameProperty());
        tourDescriptionField.textProperty().bindBidirectional(tourViewModel.descriptionProperty());
        startField.textProperty().bindBidirectional(tourViewModel.startProperty());
        destinationField.textProperty().bindBidirectional(tourViewModel.destinationProperty());
        transportTypeBox.valueProperty().bindBidirectional(tourViewModel.transportTypeProperty());

        // Zeige berechnete Werte an, wenn vorhanden
        if (distanceField != null) {
            distanceField.setText(String.format("%.2f km", tvm.getTour().getDistance()));
        }
        if (estimatedTimeField != null) {
            estimatedTimeField.setText(String.format("%.2f min", tvm.getTour().getEstimatedTime()));
        }
        // Deaktiviere Felder, die im Edit-Modus nicht verändert werden sollen
        startField.setDisable(true);
        destinationField.setDisable(true);
        transportTypeBox.setDisable(true);
    }


    @FXML
    private void onSaveButtonClick() {
        // Überprüfe, ob die Eingaben gültig sind
        InputValidator TourValidatorController = new InputValidator();
        if (!TourValidatorController.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        if (tourViewModel != null) {
            // Edit-Modus: Aktualisiere das zugrunde liegende Tour-Objekt via dem ViewModel
            tourViewModel.updateTour();
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(tourViewModel.getTour());
            }
        } else {
            // Erstellungsmodus: Neue Tour erstellen
            try {
                String start = startField.getText();
                String destination = destinationField.getText();
                String transportType = transportTypeBox.getValue();
                // Rufe den OpenRouteServiceClient auf, um Distanz und geschätzte Zeit zu ermitteln
                double[] routeDetails = OpenRouteServiceClient.getRouteDetails(start, destination, transportType);
                double distance = routeDetails[0];
                double estimatedTime = routeDetails[1];
                // Erzeuge eine neue Tour
                Tour newTour = new Tour(tourNameField.getText(), tourDescriptionField.getText(), start, destination, transportType, distance, estimatedTime);
                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(newTour);
                }
            } catch (IOException | JSONException | LocationNotFoundException e) {
                showAlert("Error retrieving route: " + e.getMessage());
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
