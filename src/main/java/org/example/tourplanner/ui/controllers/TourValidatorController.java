package org.example.tourplanner.ui.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

public class TourValidatorController {

    public static boolean validateTourInputs(TextField nameField, TextField descriptionField, TextField startField, TextField destinationField, ComboBox<String> transportTypeBox) {
        StringBuilder errorMessage = new StringBuilder();

        if (isEmpty(nameField)) {
            errorMessage.append("Name ist erforderlich!\n");
        }
        if (isEmpty(descriptionField)) {
            errorMessage.append("Beschreibung ist erforderlich!\n");
        }
        if (isEmpty(startField)) {
            errorMessage.append("Startort ist erforderlich!\n");
        }
        if (isEmpty(destinationField)) {
            errorMessage.append("Zielort ist erforderlich!\n");
        }
        if (transportTypeBox == null || transportTypeBox.getValue() == null || transportTypeBox.getValue().trim().isEmpty()) {
            errorMessage.append("Bitte wähle ein Transportmittel!\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(errorMessage.toString());
            return false;
        }
        return true;
    }

    private static boolean isEmpty(TextField field) {
        return field == null || field.getText().trim().isEmpty();
    }

    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ungültige Eingabe");
        alert.setHeaderText("Bitte korrigiere deine Eingabe");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

