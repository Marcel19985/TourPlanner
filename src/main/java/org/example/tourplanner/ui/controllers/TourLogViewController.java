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
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;

public class TourLogViewController {

    @FXML private Label ratingLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Label totalDistanceLabel;
    @FXML private Label difficultyLabel;
    @FXML private AnchorPane logDetailPane;
    @FXML private ListView<TourLogViewModel> tourLogListView;
    @FXML private Label logNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label commentLabel;

    @FXML
    public void initialize() {
        logDetailPane.setVisible(false);
        tourLogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourLogListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TourLogViewModel> call(ListView<TourLogViewModel> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TourLogViewModel tvm, boolean empty) {
                        super.updateItem(tvm, empty);
                        setText(empty || tvm == null ? null : tvm.nameProperty().get());
                    }
                };
            }
        });

        tourLogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTvm, newTvm) -> {
            if (newTvm != null) {
                showTourLogDetails(newTvm);
            } else {
                logDetailPane.setVisible(false);
                clearDetails();
            }
        });
    }

    /**
     * Statt direkt ein TourLog-Modell zu übergeben, wird hier erwartet, dass
     * der Aufrufer das ObservableList<TourLogViewModel> aus dem zugehörigen TourViewModel liefert.
     */
    public void setTourLogItems(ObservableList<TourLogViewModel> tourLogViewModels) {
        tourLogListView.setItems(tourLogViewModels);
    }

    public TourLogViewModel getSelectedTourLogViewModel() {
        return tourLogListView.getSelectionModel().getSelectedItem();
    }

    public ObservableList<TourLogViewModel> getSelectedTourLogViewModels() {
        return tourLogListView.getSelectionModel().getSelectedItems();
    }

    public void clear() {
        tourLogListView.setItems(FXCollections.observableArrayList());
        clearDetails();
    }

    public void refreshList() {
        tourLogListView.refresh();
    }

    public void showTourLogDetails(TourLogViewModel tvm) {
        if (tvm != null) {
            logDetailPane.setVisible(true);
            logNameLabel.setText(tvm.nameProperty().get());
            dateLabel.setText(tvm.dateProperty().get() != null ? tvm.dateProperty().get().toString() : "No Date");
            commentLabel.setText(tvm.commentProperty().get());
            setRatingStars(tvm.ratingProperty().get());
            totalTimeLabel.setText(String.valueOf(tvm.totalTimeProperty().get()));
            totalDistanceLabel.setText(String.valueOf(tvm.totalDistanceProperty().get()));
            difficultyLabel.setText(tvm.difficultyProperty().get());
        } else {
            logDetailPane.setVisible(false);
        }
    }

    private void setRatingStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            stars.append(i < rating ? "★" : "☆");
        }
        ratingLabel.setText(stars.toString());
    }

    public void clearDetails() {
        logNameLabel.setText("");
        dateLabel.setText("");
        commentLabel.setText("");
        ratingLabel.setText("");
        totalTimeLabel.setText("");
        totalDistanceLabel.setText("");
        difficultyLabel.setText("");
    }

    public ListView<TourLogViewModel> getTourLogListView() {
        return tourLogListView;
    }
}
