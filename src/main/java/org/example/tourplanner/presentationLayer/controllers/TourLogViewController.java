package org.example.tourplanner.presentationLayer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.businessLayer.services.TourLogService;
import org.example.tourplanner.presentationLayer.viewmodels.TourLogViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class TourLogViewController {

    @FXML private Label ratingLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Label totalDistanceLabel;
    @FXML private Label difficultyLabel;
    @FXML private AnchorPane logDetailPane;
    @FXML private ListView<TourLogViewModel> tourLogListView;
    @FXML private Label logNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label commentLabel;

    @FXML private TextField logSearchField;

    @Autowired
    private TourLogService tourLogService;

    // interne Tour‑ID, nie im UI angezeigt
    private UUID currentTourId;

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

        //Suche: Anzeige bleibt nur der Name:
        tourLogListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TourLogViewModel vm, boolean empty) {
                super.updateItem(vm, empty);
                setText(empty || vm == null ? null : vm.nameProperty().get());
            }
        });
    }

    public void refreshLogs() { //wird verwendet nachdem ein Tourlog gelöscht wird nachdem danach gesucht wurde
        String term = logSearchField.getText().trim();
        if (term.isEmpty()) {
            onClearLogs();
        } else {
            onSearchLogs();
        }
    }

    /** Wird aus MainViewController gerufen, wenn sich die Tour ändert: */
    public void setTourLogItems(UUID tourId,
                                ObservableList<TourLogViewModel> baseModels) {
        this.currentTourId = tourId;
        tourLogListView.setItems(baseModels);
    }

    @FXML
    private void onSearchLogs() {
        String term = logSearchField.getText().trim();
        List<TourLog> filtered = tourLogService.searchLogsByTour(currentTourId, term);
        ObservableList<TourLogViewModel> vms = FXCollections.observableArrayList(
                filtered.stream()
                        .map(TourLogViewModel::new)
                        .toList()
        );
        tourLogListView.setItems(vms);
        clearDetails();
    }

    @FXML
    private void onClearLogs() {
        logSearchField.clear();
        onSearchLogs(); // term == "" → lädt alle Logs
    }

    public void setTour(UUID tourId) {
        this.currentTourId = tourId;
        // Suchfeld löschen
        logSearchField.clear();
        // Alle Logs neu holen
        List<TourLog> allLogs = tourLogService.searchLogsByTour(tourId, "");
        ObservableList<TourLogViewModel> vms = FXCollections.observableArrayList(
                allLogs.stream()
                        .map(TourLogViewModel::new)
                        .toList()
        );
        tourLogListView.setItems(vms);
        clearDetails();
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
    public void clearSelection() {
        tourLogListView.getSelectionModel().clearSelection();
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
            timeLabel.setText(tvm.timeProperty().get().toString());
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
        timeLabel.setText("");
    }

    public ListView<TourLogViewModel> getTourLogListView() {
        return tourLogListView;
    }
}
