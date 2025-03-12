package org.example.tourplanner.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private TourLogViewModel tourLogViewModel;
    private MainViewController mainViewController;


    // Methode zum Initialisieren des Controllers
    @FXML
    private void initialize() {
        // Initialisieren der ComboBoxen mit Werten
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    // Setzen des Callback-Mechanismus für das Erstellen von TourLogs
    public void setOnTourLogCreatedCallback(Consumer<TourLog> callback) {
        this.onTourLogCreatedCallback = callback;
    }

    public void setCurrentTour(Tour currentTour) {
        this.currentTour = currentTour;
    }

    // Speichern des neuen TourLogs
    @FXML
    private void onSaveTourLog() {
        // Validierung der Eingaben
        if (validateTourLogInputs()) {
            try {
                String name = nameLog.getText();
                String comment = commentField.getText();
                String difficulty = difficultyComboBox.getValue();
                double totalDistance = Double.parseDouble(totalDistanceField.getText());
                double totalTime = Double.parseDouble(totalTimeField.getText());
                Integer rating = ratingComboBox.getValue();
                LocalDate date = datePicker.getValue();
                String tourName = currentTour.getName();

                // Neues TourLog erstellen
                TourLog newTourLog = new TourLog(name, tourName, date, comment, difficulty, totalDistance, totalTime, rating);

                // TourLog wird über Callback zur Liste hinzugefügt
                if (onTourLogCreatedCallback != null) {
                    onTourLogCreatedCallback.accept(newTourLog);
                    System.out.println("TourLog wurde zur Liste hinzugefügt.");
                }

                closeWindow(); // Fenster schließen
            } catch (NumberFormatException e) {
                showAlert("Ungültige Eingabe für Distanz oder Zeit.");
            }
        }
    }

    // Validierung der Eingaben
    private boolean validateTourLogInputs() {
        if (commentField.getText().isEmpty() || difficultyComboBox.getValue() == null ||
                totalDistanceField.getText().isEmpty() || totalTimeField.getText().isEmpty() ||
                ratingComboBox.getValue() == null || datePicker.getValue() == null) {
            showAlert("Please insert every field.");
            return false;
        }
        return true;
    }

    // Anzeige von Fehlermeldungen
    private void showAlert(String message) {
        // Hier kannst du eine Alert-Box oder eine andere Methode zum Zeigen von Fehlern einfügen
        System.err.println(message);
    }

    // Schließen des Fensters
    private void closeWindow() {
        Stage stage = (Stage) commentField.getScene().getWindow();
        stage.close();
    }


    public void onCancel() {
        closeWindow();
    }
}
