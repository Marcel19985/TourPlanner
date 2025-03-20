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

    @FXML private BorderPane mainPane;

    @FXML private ListView<Tour> tourListView; //Liste Tours

    @FXML private TabPane detailTabPane; //Auswahl zwischen Tour Details und Tour Logs
    @FXML private AnchorPane tourDetailsContainer; //Tour Deatils
    @FXML private AnchorPane tourLogDetailsContainer; //Tour logs

    //Create, Edit und Delete für Tour und Tourlogs:
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private MainViewModel viewModel = new MainViewModel();

    //Controller für Tour und Tourlog:
    private TourViewController tourViewController;
    private TourLogViewController tourLogViewController;

    //Mediators für Tour und Tour Log:
    private ButtonSelectionMediator<Tour> tourMediator;
    private ButtonSelectionMediator<TourLog> tourLogMediator;

// todo: UNIT TESTS HINZUFÜGEN DONE
// todo: Ersten Buchstaben unterstreichen DONE
// todo: edit und create button zusammenlegen DONE
// todo: Internationalisierung hinzufügen
    @FXML
    private void initialize() { //Initialisierung tour ListView.
        tourListView.setItems(viewModel.getTours()); //Tours laden
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Mehrfachauswahl möglich
        tourListView.setCellFactory(param -> new ListCell<Tour>() { //erstellt neue cells für jedes Item in der Liste
            @Override
            protected void updateItem(Tour tour, boolean empty) { //updated cells: wenn sie geändert werden oder z.B. Beim Scrollen durch Liste; Block mit ChatGPT erstellt
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });

        loadTourDetailView(); //Details laden
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

        //Mediators: Create, Edit und Delete Buttons aktivieren und deaktivieren je nach Auswahl
        tourMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourListView);
        tourLogMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourLogViewController.getTourLogListView());

        //Update create, delete und edit buttons je nachdem welcher Tab ausgewählt ist (TourDetails oder TourLogs)
        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {   //Listener für TabPane
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) {
                    //Setze Button-Text und Tooltip
                    createButton.setText("_Create Tour");
                    editButton.setTooltip(new Tooltip("Click to create a new tour"));
                    editButton.setText("_Edit Tour");
                    editButton.setTooltip(new Tooltip("Click to edit the selected tour"));
                    deleteButton.setText("_Delete Tour(s)");
                    deleteButton.setTooltip(new Tooltip("Click to delete the selected tour"));

                    //Aktiviere den Mediator für Tour und deaktiviere den für TourLogs.
                    tourMediator.enable();
                    tourLogMediator.disable();
                } else if (newTab.getText().equals("Tour Logs")) {
                    createButton.setText("_Create Tour Log");
                    editButton.setTooltip(new Tooltip("Click to create a new tour Log"));
                    editButton.setText("_Edit Tour Log");
                    editButton.setTooltip(new Tooltip("Click to edit the selected tour log"));
                    deleteButton.setText("_Delete Tour Log(s)");
                    deleteButton.setTooltip(new Tooltip("Click to delete the selected tour log(s)"));

                    //Aktiviere den Mediator für TourLogs und deaktiviere den für Tour.
                    tourLogMediator.enable();
                    tourMediator.disable();
                }
            }
        });

        mainPane.setOnMouseClicked(event -> { //Clear selection if user clicks outside the ListView; Block mit ChatGPT erstellt
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
    private void onCreate() { //Auswahl zwischen TabPane TourDetails oder TourLogs
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem(); //Speichere, welches TabPane aktive ist
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
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem(); //Speichere, welche Tour ausgewählt ist
        if (selectedTour == null) { //Keine Tour ausgewählt
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a tour before creating a tour log.");
            alert.showAndWait();
            return;
        }
        try { //Tour ausgewählt
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
            Parent root = loader.load();
            TourLogCreationController controller = loader.getController();
            controller.setCurrentTour(selectedTour);
            controller.setOnTourLogCreatedCallback(tourLog -> { //Callback: neue TourLog wird zur bestehenden Tour hinzugefügt; mit ChatGPT generiert
                selectedTour.addTourLog(tourLog);
                tourLogViewController.setTourForLogs(selectedTour);
                tourLogViewController.refreshList();
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
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem(); //Speichern, welches TabPane ausgewählt ist (TourDetails oder TourLogs)
        if (selectedTab != null && selectedTab.getText().equals("Tour Details")) { //Tour bearbeiten
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
        } else if (selectedTab != null && selectedTab.getText().equals("Tour Logs")) { //Tour log bearbeiten
            TourLog selectedLog = tourLogViewController.getSelectedTourLog();
            if (selectedLog == null) { //keine TourLog ausgewählt
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Log Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour log to edit.");
                alert.showAndWait();
                return;
            }
            try { //TourLog ausgewählt
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
                Parent root = loader.load();
                TourLogCreationController controller = loader.getController();
                controller.setTourLogForEditing(selectedLog); //Setze das ausgewählte TourLog in den Bearbeitungsmodus
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

        if (selectedTab.getText().equals("Tour Details")) { //Löschen von Touren
            ObservableList<Tour> selectedTours = tourListView.getSelectionModel().getSelectedItems();
            if (selectedTours.isEmpty()) {
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {//Wenn User OK bestätigt
                List<Tour> toursToDelete = new ArrayList<>(selectedTours); //Kopie der ausgewählten Touren erstellen, da die ObservableList schreibgeschützt ist
                viewModel.getTours().removeAll(toursToDelete); //Ausgewählte Tours aus Liste Löschen
                tourListView.getSelectionModel().clearSelection();
                tourViewController.setTour(null);
                tourLogViewController.clear();
            }
        } else if (selectedTab.getText().equals("Tour Logs")) { //Tour Log löschen
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