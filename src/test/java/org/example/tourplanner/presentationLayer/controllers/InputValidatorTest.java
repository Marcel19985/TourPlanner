package org.example.tourplanner.presentationLayer.controllers;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @BeforeAll
    static void initJfx() {
        //Startet das JavaFX Toolkit
        Platform.startup(() -> {});
    }
    //Wegen den alerts
    @BeforeEach
    void setTestMode() {
        InputValidator.testMode = true;
    }

    @AfterEach
    void tearDown() {
        InputValidator.testMode = false;
    }

    @Test
    public void testValidateTourInputs_AllFieldsValid() {
        TextField nameField = new TextField("Test Tour");
        TextField descriptionField = new TextField("Test Description");
        TextField startField = new TextField("Test Start");
        TextField destinationField = new TextField("Test Destination");
        ComboBox<String> transportBox = new ComboBox<>();
        transportBox.getItems().add("Car");
        transportBox.setValue("Car");

        boolean isValid = InputValidator.validateTourInputs(nameField, descriptionField, startField, destinationField, transportBox);
        assertTrue(isValid, "Die Eingabe sollte gültig sein.");
    }

    @Test
    public void testValidateTourInputs_MissingName() {
        TextField nameField = new TextField("");
        TextField descriptionField = new TextField("Test Description");
        TextField startField = new TextField("Test Start");
        TextField destinationField = new TextField("Test Destination");
        ComboBox<String> transportBox = new ComboBox<>();
        transportBox.getItems().add("Car");
        transportBox.setValue("Car");

        boolean isValid = InputValidator.validateTourInputs(nameField, descriptionField, startField, destinationField, transportBox);
        assertFalse(isValid, "Fehlender Name sollte ungültig sein.");
    }

    @Test
    public void testValidateTourInputs_MissingTransport() {
        TextField nameField = new TextField("Test Tour");
        TextField descriptionField = new TextField("Test Description");
        TextField startField = new TextField("Test Start");
        TextField destinationField = new TextField("Test Destination");
        ComboBox<String> transportBox = new ComboBox<>();
        // Kein Wert gesetzt

        boolean isValid = InputValidator.validateTourInputs(nameField, descriptionField, startField, destinationField, transportBox);
        assertFalse(isValid, "Fehlendes Transportmittel sollte ungültig sein.");
    }

    @Test
    public void testValidateTourInputs_AllFieldsEmpty() {
        TextField nameField = new TextField("");
        TextField descriptionField = new TextField("");
        TextField startField = new TextField("");
        TextField destinationField = new TextField("");
        ComboBox<String> transportBox = new ComboBox<>();
        // Kein Wert gesetzt

        boolean isValid = InputValidator.validateTourInputs(nameField, descriptionField, startField, destinationField, transportBox);
        assertFalse(isValid, "Leere Felder sollten ungültig sein.");
    }
}
