package org.example.tourplanner.ui.viewmodels;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewModel {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onSeasButtonClick() {
        welcomeText.setText("HAWEDERE");
    }

}