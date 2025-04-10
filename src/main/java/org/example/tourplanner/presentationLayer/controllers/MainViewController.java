package org.example.tourplanner.presentationLayer.controllers;

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
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.helpers.SpringContext;
import org.example.tourplanner.presentationLayer.mediators.ButtonSelectionMediator;
import org.example.tourplanner.businessLayer.services.TourService;
import org.example.tourplanner.businessLayer.services.TourLogService;
import org.example.tourplanner.presentationLayer.viewmodels.MainViewModel;
import org.example.tourplanner.presentationLayer.viewmodels.TourViewModel;
import org.example.tourplanner.presentationLayer.viewmodels.TourLogViewModel;
import javafx.collections.FXCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainViewController {

    @FXML private BorderPane mainPane;
    @FXML private ListView<TourViewModel> tourListView; //Liste mit TourViewModel
    @FXML private TabPane detailTabPane; //Auswahl zwischen Tour und TourLog Anzeige
    @FXML private AnchorPane tourDetailsContainer;
    @FXML private AnchorPane tourLogDetailsContainer;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    // Wir behalten das MainViewModel bei, um die UI-Liste zu pflegen.
    private MainViewModel viewModel = new MainViewModel();

    private TourViewController tourViewController;
    private TourLogViewController tourLogViewController;

    private ButtonSelectionMediator<TourViewModel> tourMediator;
    private ButtonSelectionMediator<TourLogViewModel> tourLogMediator;

    // Services werden via Spring injiziert
    @Autowired //autowired = es gibt in gesamter Spring application nur eine Instanz von TourService; Gegenteil = prototype
    private TourService tourService;
    @Autowired
    private TourLogService tourLogService;

    @FXML
    private void initialize() {
        // Binde die Liste an die ViewModel-Liste
        tourListView.setItems(viewModel.getTourViewModels());
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourListView.setCellFactory(param -> new ListCell<TourViewModel>() { //wird für jede Zelle in der Liste aufgerufen
            @Override
            protected void updateItem(TourViewModel tvm, boolean empty) { //wird aufgerufen, sobald ein Element in der Liste aktualisiert/geladen wird
                super.updateItem(tvm, empty);
                setText(empty || tvm == null ? null : tvm.nameProperty().get());
            }
        });

        // Lade Detail-Views
        loadTourDetailView();
        loadTourLogDetailView();

        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTVM, newTVM) -> { //Listener für TourList
            if (newTVM != null) { //neu ausgewähltes Element
                tourViewController.setTour(newTVM.getTour()); //Details der Tour werden angezeigt
                tourLogViewController.setTourLogItems(newTVM.getTourLogViewModels()); //TourLog details laden
            } else { //Wenn keine Elemente ausgewählt -> setze Tours und TourLogs auf null
                tourViewController.setTour(null);
                tourLogViewController.clearDetails();
            }
        });

        tourMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourListView);
        tourLogMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourLogViewController.getTourLogListView());

        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> { //Je nachdem ob Tour oder TourLog im tabpane ausgewählt ist
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) { //TourDetails ausgewählt -> Create, Edit und Delete werden aktualisiert
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

        mainPane.setOnMouseClicked(event -> { //Bei Klick außerhalb der Liste, wird keine Tour mehr angezeigt
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != null && !tourListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
            }
        });

        mainPane.sceneProperty().addListener((obs, oldScene, newScene) -> { //Shortcut: mit Strg + char kann man Button auswählen
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

        viewModel.getTourViewModels().setAll(
                tourService.getAllTours() //Lädt alle Touren aus der DB
                        .stream()
                        .distinct()  //Duplikate entfernen
                        .map(TourViewModel::new)
                        .toList()
        );

    } //Initialize end

    private void loadTourDetailView() { //in initialize() aufgerufen
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

    private void loadTourLogDetailView() { //in initialize() aufgerufen
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
    private void onCreate() { //Create reagiert je nach Auswahl auf Tour oder TourLog
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
            loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz));
            Parent root = loader.load();
            TourCreationController controller = loader.getController();

            controller.setOnTourCreatedCallback(newTour -> { //Tour wird in viewModel gespeichert
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
        if (selectedTVM == null) { //keine Tour ausgewählt
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a tour before creating a tour log.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
            // Wichtig: Controller über den Spring-Kontext erzeugen
            loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz));
            Parent root = loader.load();
            TourLogCreationController controller = loader.getController();
            controller.setCurrentTour(selectedTVM.getTour());
            controller.setOnTourLogCreatedCallback(tourLog -> {
                //Füge den neuen TourLog über das TourViewModel hinzu und aktualisiere die UI:
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
            if (selectedTVM == null) { //keine Tour ausgewählt
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
                loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz)); //Spring Beans in JavaFX integriert
                Parent root = loader.load();
                TourCreationController controller = loader.getController();
                //Übergib das vorhandene TourViewModel zum Bearbeiten
                controller.setTourForEditing(selectedTVM);
                controller.setOnTourUpdatedCallback(updatedTour -> {
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
                // Verwende die ControllerFactory, um den Controller als Spring Bean zu erzeugen
                loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz)); //Spring Beans in JavaFX integriert
                Parent root = loader.load();
                TourLogCreationController controller = loader.getController();
                controller.setTourLogForEditing(selectedLogVM);
                controller.setOnTourLogUpdatedCallback(updatedLog -> {
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
                // Lösche jede Tour über den Service und entferne sie aus der UI-Liste
                List<TourViewModel> toursToDelete = new ArrayList<>(selectedTVMs);
                toursToDelete.forEach(tvm -> {
                    tourService.deleteTourById(tvm.getTour().getId());
                    viewModel.getTourViewModels().remove(tvm);
                });
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
                selectedTVM.getTourLogViewModels().removeIf(vm -> selectedLogs.contains(vm.getTourLog()));
                selectedTVM.getTour().getTourLogs().removeAll(selectedLogs);

                for (TourLog log : selectedLogs) {
                    tourLogService.deleteTourLogById(log.getId());
                }

                // Aktualisiere die UI
                tourLogViewController.refreshList();
                tourLogViewController.clearDetails();
                tourLogViewController.clearSelection();
            }
        }
    }
}
