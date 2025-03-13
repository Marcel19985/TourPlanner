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

    @FXML
    public Label ratingLabel;
    @FXML
    public Label totalTimeLabel;
    @FXML
    public Label totalDistanceLabel;
    @FXML
    public Label difficultyLabel;
    @FXML
    public AnchorPane logDetailPane;
    @FXML
    private ListView<TourLog> tourLogListView;
    @FXML
    private Label logNameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label commentLabel;

    @FXML
    public void initialize() {
        logDetailPane.setVisible(false);
        // Configure the ListView cell factory for a simple display.
        tourLogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourLogListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TourLog> call(ListView<TourLog> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TourLog item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getName());
                    }
                };
            }
        });

        // Add a listener to update detail view when a tour log is selected.
        tourLogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldLog, newLog) -> {
            if (newLog != null) {
                showTourLogDetails(newLog);
            } else {
                logDetailPane.setVisible(false);
                clearDetails();
            }
        });

    }

    /**
     * Sets the items of the ListView to the tour logs of the given tour.
     */
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

    private void setRatingStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < rating) {
                stars.append("★");  // Voller Stern
            } else {
                stars.append("☆");  // Leerer Stern
            }
        }
        ratingLabel.setText(stars.toString());
    }

    void clearDetails() {
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