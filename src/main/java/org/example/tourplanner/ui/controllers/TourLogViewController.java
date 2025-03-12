package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.example.tourplanner.data.models.TourLog;

import java.time.format.DateTimeFormatter;

public class TourLogViewController {
    @FXML
    public Label tourNameInLog;
    @FXML
    private GridPane tourDetailsPane;

    @FXML
    private Label tourNameLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label commentLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label totalTimeLabel;

    @FXML
    private Label totalDistanceLabel;

    @FXML
    private Label ratingLabel;



    private TourLog currentTourLog;


    public void initialize() {
        tourDetailsPane.setVisible(false);
    }

    public void setTourLog(TourLog tourLog) {

        if (tourLog != null) {
            tourNameLabel.setText(tourLog.getName());
            if (tourLog.getDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // z. B. 10.03.2025
                dateLabel.setText(tourLog.getDate().format(formatter));
            } else {
                dateLabel.setText("Kein Datum");
            }
            commentLabel.setText(tourLog.getComment());
            difficultyLabel.setText(String.valueOf(tourLog.getDifficulty()));
            totalTimeLabel.setText(tourLog.getTotalTime() + " min");
            totalDistanceLabel.setText(tourLog.getTotalDistance() + " km");
            ratingLabel.setText(String.valueOf(tourLog.getRating()));
            tourNameInLog.setText(tourLog.getTourName());
            tourDetailsPane.setVisible(true);
        } else {
            tourDetailsPane.setVisible(false);
        }
    }


    private void clearTourLogDetails() {
        tourNameLabel.setText("");
        dateLabel.setText("");
        commentLabel.setText("");
        difficultyLabel.setText("");
        totalTimeLabel.setText("");
        totalDistanceLabel.setText("");
        ratingLabel.setText("");
    }

}
