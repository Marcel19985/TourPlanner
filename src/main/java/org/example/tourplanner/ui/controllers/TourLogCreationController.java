package org.example.tourplanner.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;

import java.time.LocalDate;
import java.util.function.Consumer;

public class TourLogCreationController {

    @FXML private TextField nameLog;
    @FXML private TextArea commentField;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField totalDistanceField;
    @FXML private TextField totalTimeField;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private DatePicker datePicker;

    private Tour currentTour;
    private Consumer<TourLog> onTourLogCreatedCallback;

    @FXML
    private void initialize() {
        // Populate ComboBoxes with values so users can select difficulty and rating.
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    public void setCurrentTour(Tour currentTour) {
        this.currentTour = currentTour;
    }

    public void setOnTourLogCreatedCallback(Consumer<TourLog> callback) {
        this.onTourLogCreatedCallback = callback;
    }

    @FXML
    private void onSaveTourLog() {
        // Validate inputs.
        if (nameLog.getText().trim().isEmpty() ||
                commentField.getText().trim().isEmpty() ||
                difficultyComboBox.getValue() == null ||
                totalDistanceField.getText().trim().isEmpty() ||
                totalTimeField.getText().trim().isEmpty() ||
                ratingComboBox.getValue() == null ||
                datePicker.getValue() == null) {

            showAlert("Please fill in all fields.");
            return;
        }

        try {
            String name = nameLog.getText();
            String comment = commentField.getText();
            String difficulty = difficultyComboBox.getValue();
            double totalDistance = Double.parseDouble(totalDistanceField.getText());
            double totalTime = Double.parseDouble(totalTimeField.getText());
            int rating = ratingComboBox.getValue();
            LocalDate date = datePicker.getValue();

            TourLog newTourLog = new TourLog(name, currentTour.getName(), date, comment, difficulty, totalDistance, totalTime, rating);

            if (onTourLogCreatedCallback != null) {
                onTourLogCreatedCallback.accept(newTourLog);
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Invalid input for distance or time.");
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameLog.getScene().getWindow();
        stage.close();
    }
}
