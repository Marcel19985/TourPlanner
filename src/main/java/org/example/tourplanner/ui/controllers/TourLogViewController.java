package org.example.tourplanner.ui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;

public class TourLogViewController {

    @FXML
    private ListView<TourLog> tourLogListView;

    // Detail labels
    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label commentLabel;

    @FXML
    public void initialize() {
        // Configure the ListView cell factory for a simple display.
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

    /**
     * Clears the ListView.
     */
    public void clear() {
        // Instead of clearing the actual tour logs,
        // set the ListView to an empty observable list.
        tourLogListView.setItems(FXCollections.observableArrayList());
        clearDetails();
    }

    /**
     * Refreshes the ListView to ensure UI updates.
     */
    public void refreshList() {
        tourLogListView.refresh();
    }

    /**
     * Update the detail pane to show the selected TourLog's details.
     */
    private void showTourLogDetails(TourLog tourLog) {
        nameLabel.setText(tourLog.getName());
        dateLabel.setText(tourLog.getDate() != null ? tourLog.getDate().toString() : "No Date");
        commentLabel.setText(tourLog.getComment());
        // Update additional details as needed.
    }

    /**
     * Clears the detail labels.
     */
    void clearDetails() {
        nameLabel.setText("");
        dateLabel.setText("");
        commentLabel.setText("");
    }
}