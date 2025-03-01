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
import org.example.tourplanner.mediators.ButtonSelectionMediator;
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

    @FXML
        private void initialize() { //wird automatisch durch FXML loader in start() aufgerufen
            tourListView.setItems(viewModel.getTours());
            tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Mehrfachauswahl der Listenelemente

            //Nur den Namen anzeigen (mit AI generiert):
            tourListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() { //mithilfe von setCellFactory kann ListView benutzerdefiniert dargestellt werden
                @Override
                protected void updateItem(Tour tour, boolean empty) {
                    super.updateItem(tour, empty);
                    setText(empty || tour == null ? null : tour.getName());
                }
            });

            // Button-Mediator initialisieren: zum "ausgrauen" von Buttons
            ButtonSelectionMediator buttonMediator = new ButtonSelectionMediator(editButton, deleteButton, tourListView);

            // Lade die Tour-Detail-Ansicht
            loadTourDetailView();
        }

        private void loadTourDetailView() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
                Parent tourView = loader.load();

                // TourView in das UI einfügen
                tourDetailsContainer.getChildren().add(tourView);
                AnchorPane.setTopAnchor(tourView, 0.0);
                AnchorPane.setBottomAnchor(tourView, 0.0);
                AnchorPane.setLeftAnchor(tourView, 0.0);
                AnchorPane.setRightAnchor(tourView, 0.0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void onCreateTour() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
                Parent root = loader.load();

                TourCreationController controller = loader.getController();
                controller.setOnTourCreatedCallback(tour -> viewModel.getTours().add(tour)); //mit AI generiert

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
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourEditView.fxml"));
                Parent root = loader.load();

                TourEditController controller = loader.getController();
                controller.setTour(selectedTour);

                controller.setOnTourUpdatedCallback(() -> {
                    tourListView.refresh();
                });

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
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tours");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewModel.getTours().removeAll(selectedTours);
            }
        }
}
