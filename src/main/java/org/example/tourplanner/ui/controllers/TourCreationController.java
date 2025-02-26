package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;

import java.util.function.Consumer;

public class TourCreationController {

    @FXML
    private TextField tourNameField;

    @FXML
    private TextField tourDescriptionField;

    private Consumer<Tour> onTourCreatedCallback;

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    @FXML
    private void onCreateButtonClick() {
        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();

        if (!name.isEmpty() && !description.isEmpty()) {
            Tour newTour = new Tour(name, description);
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
