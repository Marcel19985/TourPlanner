package org.example.tourplanner.ui.controllers;

import javafx.scene.control.*;
import java.time.LocalDate;

public class InputValidator {

    //Validation for Tour inputs
    public static boolean validateTourInputs(TextField nameField, TextField descriptionField, TextField startField, TextField destinationField, ComboBox<String> transportTypeBox) {
        StringBuilder errorMessage = new StringBuilder();

        if (isEmpty(nameField)) {
            errorMessage.append("Name is required!\n");
        }
        if (isEmpty(descriptionField)) {
            errorMessage.append("Description is required!\n");
        }
        if (isEmpty(startField)) {
            errorMessage.append("Starting location is required!\n");
        }
        if (isEmpty(destinationField)) {
            errorMessage.append("Destination is required!\n");
        }
        if (transportTypeBox == null || transportTypeBox.getValue() == null || transportTypeBox.getValue().trim().isEmpty()) {
            errorMessage.append("Please select a means of transport!\n");
        }

        return checkAndShowErrors(errorMessage);
    }

    //Validation for TourLog inputs
    public static boolean validateTourLogInputs(TextField nameLog, DatePicker dateField, TextArea commentField,
                                                ComboBox<String> difficultyBox, TextField distanceField,
                                                TextField totalTimeField, ComboBox<Integer> ratingField,
                                                Spinner <Integer> minutesField, Spinner<Integer> hoursField) {
        StringBuilder errorMessage = new StringBuilder();

        if (isEmpty(nameLog)) {
            errorMessage.append("Name is required!\n");
        }

        if (dateField == null || dateField.getValue() == null) {
            errorMessage.append("Date is required!\n");
        } else if (dateField.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("Date cannot be in the future!\n");
        }

        // Überprüfen der Minuten und Stunden Spinner-Werte, ob sie gültige Zahlen sind
        if (minutesField.getValue() == null || minutesField.getValue() < 0 || minutesField.getValue() > 59) {
            errorMessage.append("Minutes must be a valid number between 0 and 59!\n");
        }
        if (hoursField.getValue() == null || hoursField.getValue() < 0 || hoursField.getValue() > 23) {
            errorMessage.append("Hours must be a valid number between 0 and 23!\n");
        }

        if (isEmpty(commentField)) {
            errorMessage.append("Comment is required!\n");
        }

        if (difficultyBox == null || difficultyBox.getValue() == null || difficultyBox.getValue().trim().isEmpty()) {
            errorMessage.append("Please select a difficulty level!\n");
        }

        if (isEmpty(distanceField) || !isValidNumber(distanceField.getText())) {
            errorMessage.append("Distance must be a valid number!\n");
        }

        if (isEmpty(totalTimeField) || !isValidNumber(totalTimeField.getText())) {
            errorMessage.append("Total time must be a valid number!\n");
        }

        if (ratingField == null || ratingField.getValue() == null || ratingField.getValue() < 1 || ratingField.getValue() > 10) {
            errorMessage.append("Rating is required!\n");
        }

        return checkAndShowErrors(errorMessage);
    }


    //Helper methods
    private static boolean isEmpty(TextField field) {
        return field == null || field.getText().trim().isEmpty();
    }

    private static boolean isEmpty(TextArea field) {
        return field == null || field.getText().trim().isEmpty();
    }

    private static boolean isValidNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    private static boolean checkAndShowErrors(StringBuilder errorMessage) {
        if (errorMessage.length() > 0) {
            showAlert(errorMessage.toString());
            return false;
        }
        return true;
    }

    static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Please correct your input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
