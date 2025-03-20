package org.example.tourplanner.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.mediators.ButtonSelectionMediator;

public class TourLogViewController {

    @FXML public Label ratingLabel;
    @FXML public Label totalTimeLabel;
    @FXML public Label totalDistanceLabel;
    @FXML public Label difficultyLabel;
    @FXML public AnchorPane logDetailPane;
    @FXML private ListView<TourLog> tourLogListView;
    @FXML private Label logNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label commentLabel;

    @FXML
    public void initialize() {
        logDetailPane.setVisible(false); //DetailPane verstecken, da noch kein TourLog ausgewählt
        tourLogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Mehrfachauswahl möglich
        tourLogListView.setCellFactory(new Callback<>() { //neue ListCell, mit CHatGPT generiert
            @Override
            public ListCell<TourLog> call(ListView<TourLog> param) { //Überschreibt, wie Items aktualisiert werden
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TourLog item, boolean empty) {
                        super.updateItem(item, empty); //Ruft Elternklasse auf
                        setText(empty || item == null ? null : item.getName()); //wenn Zelle leer oder kein TourLog Objekt vorhanden, setzte den Text auf NULL
                    }
                };
            }
        });

        tourLogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldLog, newLog) -> { //Listerner zu selectedItemProperty hinzufügen: reagiert sobald sich die Auswahl in der ListView ändert
            if (newLog != null) { //wenn TourLog ausgewählt wurde, werden Details des ausgewählten Log angezeigt
                showTourLogDetails(newLog);
            } else { //wenn kein TourLog ausgewählt
                logDetailPane.setVisible(false);
                clearDetails();
            }
        });
    }

    public void setTourForLogs(Tour tour) {
        if (tour != null) {
            tourLogListView.setItems(tour.getTourLogs());
        }
    }

    public TourLog getSelectedTourLog() {
        return tourLogListView.getSelectionModel().getSelectedItem();
    }

    public ObservableList<TourLog> getSelectedTourLogs() {
        return tourLogListView.getSelectionModel().getSelectedItems();
    }


    public void clear() {
        tourLogListView.setItems(FXCollections.observableArrayList());
        clearDetails();
    }


    public void refreshList() {
        tourLogListView.refresh();
    }


    void showTourLogDetails(TourLog tourLog) {
        if (tourLog != null) {
            logDetailPane.setVisible(true);
            logNameLabel.setText(tourLog.getName());
            dateLabel.setText(tourLog.getDate() != null ? tourLog.getDate().toString() : "No Date");
            commentLabel.setText(tourLog.getComment());
            setRatingStars(tourLog.getRating());
            totalTimeLabel.setText(String.valueOf(tourLog.getTotalTime()));
            totalDistanceLabel.setText(String.valueOf(tourLog.getTotalDistance()));
            difficultyLabel.setText(String.valueOf(tourLog.getDifficulty()));
        } else {
            logDetailPane.setVisible(false);

        }
    }

    private void setRatingStars(int rating) { //Anzeige für Rating, mit ChatGPT generiert
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < rating) {
                stars.append("★");  //Voller Stern
            } else {
                stars.append("☆");  //Leerer Stern
            }
        }
        ratingLabel.setText(stars.toString());
    }

    void clearDetails() { //Felder leeren:
        logNameLabel.setText("");
        dateLabel.setText("");
        commentLabel.setText("");
        ratingLabel.setText("");
        totalTimeLabel.setText("");
        totalDistanceLabel.setText("");
        difficultyLabel.setText("");
    }

    public ListView<TourLog> getTourLogListView() {
        return tourLogListView;
    }

}