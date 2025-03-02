package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.json.JSONException;

import java.io.IOException;
import java.util.function.Consumer;

import static org.example.tourplanner.ui.controllers.TourValidatorController.showAlert;

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
    private void initialize() { //initialize wird automatisch von FXML loader aufgerufen
        //Werte für Transportmittel in fxml hinzufügen
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
    }

    //wird aufgerufen nach dem Erstellen einer Tour
    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    @FXML
    private void onCreateButtonClick() { //wenn create Button geklickt:
        if (TourValidatorController.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            try {
                String name = tourNameField.getText();
                String description = tourDescriptionField.getText();
                String start = startField.getText();
                String destination = destinationField.getText();
                String transportType = transportTypeBox.getValue();

                System.out.println("Tour wird erstellt: " + name + ", von " + start + " nach " + destination + " mit " + transportType); //debug

                //Maybe in Zukunft route Objekt hin und her geben?
                double[] routeDetails = OpenRouteServiceClient.getRouteDetails(start, destination, transportType); //Anfrage an openrouteservice
                double distance = routeDetails[0]; //Distanz in Kilometer
                double estimatedTime = routeDetails[1]; //Dauer in Minuten

                System.out.println("Distanz: " + distance + " km, Geschätzte Zeit: " + estimatedTime + " min"); //debug

                Tour newTour = new Tour(name, description, start, destination, transportType, distance, estimatedTime);

                if (onTourCreatedCallback != null) { //Tour wird über Callback zur Liste hinzugefügt
                    onTourCreatedCallback.accept(newTour);
                    System.out.println("Tour wurde zur Liste hinzugefügt.");
                }

                closeWindow();
            } catch (IOException e) {
                System.err.println("Fehler beim Abrufen der Route: " + e.getMessage());
                showAlert("Fehler beim Abrufen der Route: " + e.getMessage());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @FXML
    private void onCancelButtonClick() {
        closeWindow();
    } //Klick auf Cancel Button

    private void closeWindow() {
        Stage stage = (Stage) tourNameField.getScene().getWindow();
        stage.close();
    }
}
