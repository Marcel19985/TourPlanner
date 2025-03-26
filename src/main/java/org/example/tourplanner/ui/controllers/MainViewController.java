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
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.mediators.ButtonSelectionMediator;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import org.example.tourplanner.ui.viewmodels.TourViewModel;
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML private BorderPane mainPane;
    @FXML private ListView<TourViewModel> tourListView; // Liste nun mit TourViewModel
    @FXML private TabPane detailTabPane;
    @FXML private AnchorPane tourDetailsContainer;
    @FXML private AnchorPane tourLogDetailsContainer;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private MainViewModel viewModel = new MainViewModel();

    private TourViewController tourViewController;
    private TourLogViewController tourLogViewController;

    private ButtonSelectionMediator<TourViewModel> tourMediator;
    private ButtonSelectionMediator<TourLogViewModel> tourLogMediator;

    @FXML
    private void initialize() {
        // Binde die Liste an die ViewModel-Liste
        tourListView.setItems(viewModel.getTours());
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourListView.setCellFactory(param -> new ListCell<TourViewModel>() {
            @Override
            protected void updateItem(TourViewModel tvm, boolean empty) {
                super.updateItem(tvm, empty);
                setText(empty || tvm == null ? null : tvm.nameProperty().get());
            }
        });


        loadTourDetailView();
        loadTourLogDetailView();

        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTVM, newTVM) -> {
            if (newTVM != null) {
                tourViewController.setTour(newTVM.getTour());
                tourLogViewController.setTourLogItems(newTVM.getTourLogViewModels());
            } else {
                tourViewController.setTour(null);
                tourLogViewController.clearDetails();
            }
        });

        tourMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourListView);
        tourLogMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourLogViewController.getTourLogListView());

        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) {
                    createButton.setText("_Create Tour");
                    editButton.setText("_Edit Tour");
                    deleteButton.setText("_Delete Tour(s)");
                    tourMediator.enable();
                    tourLogMediator.disable();
                } else if (newTab.getText().equals("Tour Logs")) {
                    createButton.setText("_Create Tour Log");
                    editButton.setText("_Edit Tour Log");
                    deleteButton.setText("_Delete Tour Log(s)");
                    tourLogMediator.enable();
                    tourMediator.disable();
                }
            }
        });

        mainPane.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != null && !tourListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
            }
        });

        mainPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
                        this::onCreate
                );
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN),
                        this::onEdit
                );
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
                        this::onDelete
                );
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
    private void onCreate() {
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
            // Bei der Erstellung eines neuen Tours wird kein ViewModel übergeben,
            // da im Erstellungsmodus tourViewModel == null ist.
            controller.setOnTourCreatedCallback(newTour -> {
                // Füge das neue Tour-Objekt über sein ViewModel der ObservableList hinzu.
                viewModel.addTour(newTour);
            });
            Stage stage = new Stage();
            stage.setTitle("Create New Tour");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCreateTourLog() {
        TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTVM == null) {
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
            controller.setCurrentTour(selectedTVM.getTour()); // falls benötigt
            controller.setOnTourLogCreatedCallback(tourLog -> {
                // Füge den neuen TourLog über das TourViewModel hinzu – intern wird auch das ViewModel aktualisiert
                selectedTVM.addTourLog(tourLog);
                tourLogViewController.setTourLogItems(selectedTVM.getTourLogViewModels());
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
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getText().equals("Tour Details")) {
            TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTVM == null) {
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
                Parent root = loader.load();
                TourCreationController controller = loader.getController();
                // Übergib direkt das vorhandene TourViewModel
                controller.setTourForEditing(selectedTVM);
                controller.setOnTourUpdatedCallback(updatedTour -> {
                    // Da beide Controller mit derselben TourViewModel-Instanz arbeiten, sind die Änderungen bereits übernommen.
                    tourListView.refresh();
                    tourViewController.setTour(selectedTVM.getTour());
                });
                Stage stage = new Stage();
                stage.setTitle("Edit Tour");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectedTab != null && selectedTab.getText().equals("Tour Logs")) {
            // Beispiel aus MainViewController beim Editieren eines TourLogs
            TourLogViewModel selectedLogVM = tourLogViewController.getSelectedTourLogViewModel();
            if (selectedLogVM == null) {
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
                // Hier wird das bestehende TourLogViewModel übergeben – nicht eine neue Instanz!
                controller.setTourLogForEditing(selectedLogVM);
                controller.setOnTourLogUpdatedCallback(updatedLog -> {
                    // Nachdem der Bearbeitungsdialog geschlossen wird, können wir die Liste aktualisieren.
                    tourLogViewController.refreshList();
                    tourLogViewController.showTourLogDetails(selectedLogVM);
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
            ObservableList<TourViewModel> selectedTVMs = tourListView.getSelectionModel().getSelectedItems();
            if (selectedTVMs.isEmpty()) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                List<TourViewModel> toursToDelete = new ArrayList<>(selectedTVMs);
                viewModel.deleteTours(toursToDelete);
                tourListView.getSelectionModel().clearSelection();
                tourViewController.setTour(null);
                tourLogViewController.clear();
            }
        } else if (selectedTab.getText().equals("Tour Logs")) {
            TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTVM == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour first.");
                alert.showAndWait();
                return;
            }
            ObservableList<TourLogViewModel> selectedLogVMs = tourLogViewController.getSelectedTourLogViewModels();
            ObservableList<TourLog> selectedLogs = FXCollections.observableArrayList();
            for (TourLogViewModel vm : selectedLogVMs) {
                selectedLogs.add(vm.getTourLog());
            }
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
                // Zuerst die ViewModel-Liste aktualisieren:
                selectedTVM.getTourLogViewModels().removeIf(vm -> selectedLogs.contains(vm.getTourLog()));
                        // Dann den TourLog aus dem zugrunde liegenden Datenmodell entfernen:
                selectedTVM.getTour().getTourLogs().removeAll(new ArrayList<>(selectedLogs));

                // Danach die UI aktualisieren:
                tourLogViewController.refreshList();
                tourLogViewController.clearDetails();
                tourLogViewController.clearSelection();

            }
        }
    }
}
