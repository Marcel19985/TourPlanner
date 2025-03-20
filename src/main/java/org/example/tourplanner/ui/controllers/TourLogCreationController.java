package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
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

    private Tour currentTour; // Wird im Erstellungsmodus benötigt, um den Bezug zur Tour herzustellen
    private Consumer<TourLog> onTourLogCreatedCallback; //Callback für den Erstellungsmodus
    private Consumer<TourLog> onTourLogUpdatedCallback; //Callback für den Bearbeitungsmodus
    private TourLog editingTourLog = null; //Wird im Bearbeitungsmodus gesetzt – ist null, wenn ein neues Log erstellt wird

    @FXML
    private void initialize() {
        //Befüllen der ComboBoxes
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    }

    //Wird im Erstellungsmodus aufgerufen, um den Bezug zur Tour zu setzen:
    public void setCurrentTour(Tour currentTour) {
        this.currentTour = currentTour;
    }

    //Wird im Bearbeitungsmodus aufgerufen, um das zu editierende TourLog zu setzen und die Felder mit den existierenden Werten zu füllen:
    public void setTourLogForEditing(TourLog tourLog) {
        this.editingTourLog = tourLog;
        nameLog.setText(tourLog.getName());
        commentField.setText(tourLog.getComment());
        difficultyComboBox.setValue(tourLog.getDifficulty());
        totalDistanceField.setText(String.valueOf(tourLog.getTotalDistance()));
        totalTimeField.setText(String.valueOf(tourLog.getTotalTime()));
        ratingComboBox.setValue(tourLog.getRating());
        datePicker.setValue(tourLog.getDate());
    }

    public void setOnTourLogCreatedCallback(Consumer<TourLog> callback) {
        this.onTourLogCreatedCallback = callback;
    }

    public void setOnTourLogUpdatedCallback(Consumer<TourLog> callback) {
        this.onTourLogUpdatedCallback = callback;
    }

    @FXML
    private void onSaveTourLog() {
        //Validierung: Alle Felder müssen ausgefüllt sein
        if (!InputValidator.validateTourLogInputs(nameLog, datePicker, commentField, difficultyComboBox, totalDistanceField, totalTimeField, ratingComboBox)) {
            return;
        }

        try {
            if (editingTourLog != null) {
                //Bearbeitungsmodus: Bestehendes TourLog aktualisieren
                editingTourLog.setName(nameLog.getText());
                editingTourLog.setComment(commentField.getText());
                editingTourLog.setDifficulty(difficultyComboBox.getValue());
                editingTourLog.setTotalDistance(Double.parseDouble(totalDistanceField.getText()));
                editingTourLog.setTotalTime(Double.parseDouble(totalTimeField.getText()));
                editingTourLog.setRating(ratingComboBox.getValue());
                editingTourLog.setDate(datePicker.getValue());
                if (onTourLogUpdatedCallback != null) {
                    onTourLogUpdatedCallback.accept(editingTourLog);
                }
            } else {
                //Erstellungsmodus: Neues TourLog erzeugen
                String name = nameLog.getText();
                String comment = commentField.getText();
                String difficulty = difficultyComboBox.getValue();
                double totalDistance = Double.parseDouble(totalDistanceField.getText());
                double totalTime = Double.parseDouble(totalTimeField.getText());
                int rating = ratingComboBox.getValue();
                LocalDate date = datePicker.getValue();

                TourLog newTourLog = new TourLog(name, date, comment, difficulty, totalDistance, totalTime, rating);
                // Falls currentTour gesetzt ist, kann hier der Tour-Name übernommen werden:
                if (currentTour != null) {
                    newTourLog.setTourName(currentTour.getName());
                }
                if (onTourLogCreatedCallback != null) {
                    onTourLogCreatedCallback.accept(newTourLog);
                }
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
