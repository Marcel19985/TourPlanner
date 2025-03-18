package org.example.tourplanner.ui.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.mediators.ButtonSelectionMediator;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private ListView<Tour> tourListView;

    @FXML
    private TabPane detailTabPane; //Rechte Seite von Main

    // Containers for each detail view (loaded into the respective Tab)
    @FXML
    private AnchorPane tourDetailsContainer;

    @FXML
    private AnchorPane tourLogDetailsContainer;

    @FXML
    private Button createButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private MainViewModel viewModel = new MainViewModel();

    // Controllers for the two detail views
    private TourViewController tourViewController;
    private TourLogViewController tourLogViewController;

    private ButtonSelectionMediator<Tour> tourMediator;
    private ButtonSelectionMediator<TourLog> tourLogMediator;

// todo: UNIT TESTS HINZUFÜGEN DONE
// todo: Ersten Buchstaben unterstreichen DONE
// todo: edit und create button zusammenlegen DONE
// todo: Internationalisierung hinzufügen
    @FXML
    private void initialize() {
        // Initialize the tours ListView.
        tourListView.setItems(viewModel.getTours());
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourListView.setCellFactory(param -> new ListCell<Tour>() { //erstellt neue cells für jedes Item in der Liste
            @Override
            protected void updateItem(Tour tour, boolean empty) { //updated cells: wenn sie geändert werden oder z.B. Beim Scrollen durch Liste
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });

        // Load the detail views into their respective containers.
        loadTourDetailView();
        loadTourLogDetailView();

        //listener für Tour Auswahl: sobald eine liste ausgewählt oder abgewählt wird:
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTour, newTour) -> {
            if (newTour != null) { //neue Tour ausgewählt
                tourViewController.setTour(newTour); //tour details updaten
                tourLogViewController.setTourForLogs(newTour); //tour logs updaten
            } else { //keine Tour ausgewählt
                tourViewController.setTour(null); //keine Tour angezeigt
                tourLogViewController.clearDetails(); //keine Tour Details angezeigt
            }
        });

        tourMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourListView);
        tourLogMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourLogViewController.getTourLogListView());

        //Update create, delete und edit buttons je nachdem welcher Tab ausgewählt ist (TourDetails oder TourLogs)
        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) {
                    // Setze Button-Text und Tooltip
                    createButton.setText("_Create Tour");
                    editButton.setTooltip(new Tooltip("Click to create a new tour"));
                    editButton.setText("_Edit Tour");
                    editButton.setTooltip(new Tooltip("Click to edit the selected tour"));
                    deleteButton.setText("_Delete Tour(s)");
                    deleteButton.setTooltip(new Tooltip("Click to delete the selected tour"));

                    // Aktiviere den Mediator für Tour und deaktiviere den für TourLogs.
                    tourMediator.enable();
                    tourLogMediator.disable();
                } else if (newTab.getText().equals("Tour Logs")) {
                    createButton.setText("_Create Tour Log");
                    editButton.setTooltip(new Tooltip("Click to create a new tour Log"));
                    editButton.setText("_Edit Tour Log");
                    editButton.setTooltip(new Tooltip("Click to edit the selected tour log"));
                    deleteButton.setText("_Delete Tour Log(s)");
                    deleteButton.setTooltip(new Tooltip("Click to delete the selected tour log(s)"));

                    // Aktiviere den Mediator für TourLogs und deaktiviere den für Tour.
                    tourLogMediator.enable();
                    tourMediator.disable();
                }
            }
        });

        // Clear selection if user clicks outside the ListView.
        mainPane.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != null && !tourListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
            }
        });

        mainPane.sceneProperty().addListener((obs, oldScene, newScene) -> { //Keyboard Shortcuts
            if (newScene != null) {
                //STRG+N -> onCreate
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
                        this::onCreate
                );

                //STRG+E -> onEdit
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN),
                        this::onEdit
                );

                //STRG+D -> onDelete
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
                        this::onDelete
                );

                // oder ENTF -> onDelete
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.DELETE),
                        this::onDelete
                );
            }
        });
    }

    private void loadTourDetailView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
            Parent tourView = loader.load();
            tourViewController = loader.getController();
            // Set the view model on the TourViewController.
            tourViewController.setViewModel(viewModel);
            tourDetailsContainer.getChildren().add(tourView);
            AnchorPane.setTopAnchor(tourView, 0.0);
            AnchorPane.setBottomAnchor(tourView, 0.0);
            AnchorPane.setLeftAnchor(tourView, 0.0);
            AnchorPane.setRightAnchor(tourView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTourLogDetailView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogSplitView.fxml"));
            Parent tourLogSplitView = loader.load();
            tourLogViewController = loader.getController();
            tourLogDetailsContainer.getChildren().add(tourLogSplitView);
            AnchorPane.setTopAnchor(tourLogSplitView, 0.0);
            AnchorPane.setBottomAnchor(tourLogSplitView, 0.0);
            AnchorPane.setLeftAnchor(tourLogSplitView, 0.0);
            AnchorPane.setRightAnchor(tourLogSplitView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreate() {
        // Determine which detail tab is active.
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.getText().equals("Tour Details")) {
            onCreateTour();
        } else if (selectedTab.getText().equals("Tour Logs")) {
            onCreateTourLog();
        }
    }

    private void onCreateTour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
            Parent root = loader.load();
            TourCreationController controller = loader.getController();
            controller.setOnTourCreatedCallback(tour -> viewModel.getTours().add(tour));
            Stage stage = new Stage();
            stage.setTitle("Create New Tour");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCreateTourLog() {
        // Retrieve the selected tour.
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a tour before creating a tour log.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
            Parent root = loader.load();
            TourLogCreationController controller = loader.getController();
            controller.setCurrentTour(selectedTour);
            // Set the callback so that the new tour log is added to the selected tour.
            controller.setOnTourLogCreatedCallback(tourLog -> {
                System.out.println("Callback: Adding TourLog " + tourLog.getName() + " to Tour " + selectedTour.getName());
                selectedTour.addTourLog(tourLog);
                // Reassign the list (this is usually not needed with an ObservableList, but we force it here)
                tourLogViewController.setTourForLogs(selectedTour);
                tourLogViewController.refreshList();
                System.out.println("TourLog count: " + selectedTour.getTourLogs().size());
            });
            Stage stage = new Stage();
            stage.setTitle("Create Tour Log");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getText().equals("Tour Details")) {
            // Bearbeiten einer Tour (bestehender Code)
            Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTour == null) {
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
                Parent root = loader.load();
                TourCreationController controller = loader.getController();
                controller.setTourForEditing(selectedTour);
                controller.setOnTourUpdatedCallback(updatedTour -> {
                    tourListView.refresh();
                    Tour selected = tourListView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        tourViewController.setTour(selected);
                    }
                });
                Stage stage = new Stage();
                stage.setTitle("Edit Tour");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectedTab != null && selectedTab.getText().equals("Tour Logs")) {
            // Bearbeiten eines TourLogs
            TourLog selectedLog = tourLogViewController.getSelectedTourLog();
            if (selectedLog == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Log Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour log to edit.");
                alert.showAndWait();
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
                Parent root = loader.load();
                TourLogCreationController controller = loader.getController();
                // Setze das ausgewählte TourLog in den Bearbeitungsmodus
                controller.setTourLogForEditing(selectedLog);
                controller.setOnTourLogUpdatedCallback(updatedLog -> {
                    tourLogViewController.refreshList();
                    tourLogViewController.showTourLogDetails(updatedLog);
                });
                Stage stage = new Stage();
                stage.setTitle("Edit Tour Log");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onDelete() {
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return;
        }

        if (selectedTab.getText().equals("Tour Details")) {
            // Löschen von Touren
            ObservableList<Tour> selectedTours = tourListView.getSelectionModel().getSelectedItems();
            if (selectedTours.isEmpty()) {
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Kopie der ausgewählten Touren erstellen, da die ObservableList schreibgeschützt ist.
                List<Tour> toursToDelete = new ArrayList<>(selectedTours);
                viewModel.getTours().removeAll(toursToDelete);
                tourListView.getSelectionModel().clearSelection();
                tourViewController.setTour(null);
                tourLogViewController.clear();
            }
        } else if (selectedTab.getText().equals("Tour Logs")) {
            // Bestehender Code zum Löschen von TourLogs (wie zuvor implementiert)
            Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTour == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour first.");
                alert.showAndWait();
                return;
            }

            ObservableList<TourLog> selectedLogs = tourLogViewController.getSelectedTourLogs();
            if (selectedLogs.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Log Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select at least one tour log to delete.");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour Log(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedTour.getTourLogs().removeAll(new ArrayList<>(selectedLogs));
                tourLogViewController.refreshList();
                tourLogViewController.clearDetails();
            }
        }
    }

}