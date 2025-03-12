package org.example.tourplanner.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.mediators.ButtonSelectionMediator;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.util.Optional;

public class MainViewController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private ListView<Tour> tourListView;

    // The TabPane on the right side
    @FXML
    private TabPane detailTabPane;

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

    @FXML
    private void initialize() {
        // Initialize the tours ListView.
        tourListView.setItems(viewModel.getTours());
        tourListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        tourListView.setCellFactory(param -> new ListCell<Tour>() {
            @Override
            protected void updateItem(Tour tour, boolean empty) {
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });

        // Load the detail views into their respective containers.
        loadTourDetailView();
        loadTourLogDetailView();

        // Combined listener for tour selection:
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTour, newTour) -> {
            if (newTour != null) {
                tourViewController.setTour(newTour);
                tourLogViewController.setTourForLogs(newTour);
            } else {
                tourViewController.setTour(null);
                // Option 1: call clear()—but then you must reassign on selection.
                // Option 2: only clear details:
                tourLogViewController.clearDetails();
            }
        });

        // Listener for tab selection to update the button label.
        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) {
                    createButton.setText("Create Tour");
                    createButton.setTooltip(new Tooltip("Click to create a new tour"));
                } else if (newTab.getText().equals("Tour Logs")) {
                    createButton.setText("Create Tour Log");
                    createButton.setTooltip(new Tooltip("Click to create a new tour log for the selected tour"));
                }
            }
        });

        // Set the initial label based on the currently selected tab.
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null && !detailTabPane.getTabs().isEmpty()) {
            selectedTab = detailTabPane.getTabs().get(0);
            detailTabPane.getSelectionModel().select(selectedTab);
        }
        if (selectedTab != null) {
            if (selectedTab.getText().equals("Tour Details")) {
                createButton.setText("Create Tour");
                createButton.setTooltip(new Tooltip("Click to create a new tour"));
            } else if (selectedTab.getText().equals("Tour Logs")) {
                createButton.setText("Create Tour Log");
                createButton.setTooltip(new Tooltip("Click to create a new tour log for the selected tour"));
            }
        }

        // Use a mediator to update button states.
        new ButtonSelectionMediator(editButton, deleteButton, tourListView);

        // Clear selection if user clicks outside the ListView.
        mainPane.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != null && !tourListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
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
                Tour updatedTour = tourListView.getSelectionModel().getSelectedItem();
                if (updatedTour != null) {
                    tourViewController.setTour(updatedTour);
                }
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
    private void onDelete() {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Tour");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone!");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewModel.getTours().remove(selectedTour);
        }
    }
}