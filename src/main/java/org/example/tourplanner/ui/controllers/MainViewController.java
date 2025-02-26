package org.example.tourplanner.ui.controllers;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.MainViewModel;

import java.io.IOException;
import java.util.Optional;

public class MainViewController {

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private ListView<Tour> tourListView;

    @FXML
    private AnchorPane tourDetailsContainer;

    private final MainViewModel viewModel = new MainViewModel();
    private TourViewController tourViewController;

    @FXML
    private void initialize() {
        tourListView.setItems(viewModel.getTours());
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Nur den Namen anzeigen
        tourListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Tour tour, boolean empty) {
                super.updateItem(tour, empty);
                if (empty || tour == null) {
                    setText(null);
                } else {
                    setText(tour.getName());
                }
            }
        });
        deleteButton.disableProperty().bind(tourListView.getSelectionModel().selectedItemProperty().isNull());
        // Update der Button-Logik, wenn sich die Auswahl ändert
        // Listener für Änderungen in der Auswahl
        tourListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tour>) change -> {
            updateEditButtonState();
        });

        updateEditButtonState(); // Initial setzen


        // Lade die Tour-Detail-Ansicht (TourView.fxml)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
            Node tourView = loader.load();
            tourViewController = loader.getController(); // Setze die globale Variable

            // TourView in das UI einfügen
            tourDetailsContainer.getChildren().add(tourView);
            AnchorPane.setTopAnchor(tourView, 0.0);
            AnchorPane.setBottomAnchor(tourView, 0.0);
            AnchorPane.setLeftAnchor(tourView, 0.0);
            AnchorPane.setRightAnchor(tourView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setze den Event-Listener für die Auswahl
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (tourViewController != null) {
                tourViewController.setTour(newSelection);
            }
        });

    }

    private void updateEditButtonState() {
        int selectedCount = tourListView.getSelectionModel().getSelectedItems().size();
        editButton.setDisable(selectedCount != 1);
    }

    @FXML
    private void onCreateTour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
            Parent root = loader.load();

            TourCreationController controller = loader.getController();
            controller.setOnTourCreatedCallback(tour -> viewModel.getTours().add(tour));

            Stage stage = new Stage();
            stage.setTitle("Create new Tour");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditTour() {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            System.out.println("Keine Tour ausgewählt!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourEditView.fxml"));
            Parent root = loader.load();

            // Controller holen und Tour übergeben
            TourEditController controller = loader.getController();
            controller.setTour(selectedTour);

            // Callback setzen
            controller.setOnTourUpdatedCallback(() -> {
                tourListView.refresh();
                tourViewController.setTour(selectedTour); // Falls gerade geöffnet, direkt updaten
            });

            // Fenster anzeigen
            Stage stage = new Stage();
            stage.setTitle("Edit Tour");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void onDeleteTour() {
        var selectedTours = tourListView.getSelectionModel().getSelectedItems();

        if (selectedTours.isEmpty()) {
            System.out.println("Keine Tour ausgewählt!");
            return;
        }

        // Bestätigungsdialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Tours");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Entferne alle ausgewählten Touren
            viewModel.getTours().removeAll(selectedTours);

            // Falls eine der gelöschten Touren angezeigt wurde → Ansicht leeren
            tourViewController.setTour(null);
        }
    }


}
