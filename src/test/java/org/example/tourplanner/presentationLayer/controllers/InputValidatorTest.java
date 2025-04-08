package org.example.tourplanner.presentationLayer.controllers;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @BeforeAll
    static void initJfx() {
        //Startet das JavaFX Toolkit
        Platform.startup(() -> {});
    }

    @AfterEach
    void tearDown() {
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
}