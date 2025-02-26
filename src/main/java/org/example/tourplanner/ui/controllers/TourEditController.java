package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;

public class TourEditController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    private Tour tour; // Referenz zur aktuellen Tour

    private Runnable onTourUpdatedCallback;


    public void setTour(Tour tour) {
        this.tour = tour;
        nameField.setText(tour.getName());
        descriptionField.setText(tour.getDescription());
    }


    public void setOnTourUpdatedCallback(Runnable callback) {
        this.onTourUpdatedCallback = callback;
    }

    @FXML
    private void onSave() {
        if (tour != null) {
            tour.setName(nameField.getText());
            tour.setDescription(descriptionField.getText());

            // Callback aufrufen, um MainView zu informieren
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.run();
            }

            // Fenster schließen
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        }
    }


    @FXML
    private void onCancel() {
        // Fenster schließen ohne Änderungen
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
