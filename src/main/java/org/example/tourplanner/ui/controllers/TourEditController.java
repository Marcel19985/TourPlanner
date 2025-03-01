package org.example.tourplanner.ui.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.TourViewModel;

public class TourEditController {

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;

    private TourViewModel tourViewModel;
    private Runnable onTourUpdatedCallback;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Zu Fuß", "Auto", "Fahrrad");
    }

    public void setTour(Tour tour) {
        this.tourViewModel = new TourViewModel(tour);

        nameField.textProperty().bindBidirectional(tourViewModel.nameProperty());
        descriptionField.textProperty().bindBidirectional(tourViewModel.descriptionProperty());
        startField.textProperty().bindBidirectional(tourViewModel.startProperty());
        destinationField.textProperty().bindBidirectional(tourViewModel.destinationProperty());
        transportTypeBox.valueProperty().bindBidirectional(tourViewModel.transportTypeProperty());
    }

    public void setOnTourUpdatedCallback(Runnable callback) {
        this.onTourUpdatedCallback = callback;
    }

    @FXML
    private void onSave() {
        if (TourValidatorController.validateTourInputs(nameField, descriptionField, startField, destinationField, transportTypeBox)) {
            tourViewModel.updateTour();

            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.run();
            }

            closeWindow();
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}

