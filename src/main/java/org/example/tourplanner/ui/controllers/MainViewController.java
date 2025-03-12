package org.example.tourplanner.ui.controllers;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.mediators.ButtonSelectionMediator;
import org.example.tourplanner.ui.viewmodels.MainViewModel;
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;

import java.io.IOException;
import java.util.Optional;

public class MainViewController {

    @FXML
    public BorderPane mainPane;
    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Label labelTours;

    @FXML
    private Label labelTourLogs;

    @FXML
    private ListView<Tour> tourListView; //Liste aller Tournamen

    @FXML
    private ListView<TourLog> tourLogListView; //Liste der Tourlogs

    @FXML //Detailansicht von ausgewählter Tour
    private AnchorPane tourDetailsContainer;


    private MainViewModel viewModel = new MainViewModel();

    //Referenz auf den TourViewController (Detailansicht)
    private TourViewController tourViewController = new TourViewController();
    private TourLogViewController tourLogViewController = new TourLogViewController();

//todo: bei edit und delete unterscheiden ob gerade eine tour oder ein tourlog ausgewählt ist
    @FXML
    private void initialize() {
        mainPane.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();

            // Prüfen, ob NICHT in die ListViews geklickt wurde
            if (clickedNode != null && !tourListView.equals(clickedNode) && !tourLogListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
                tourLogListView.getSelectionModel().clearSelection();
            }
        });

        // Tour-Liste initialisieren
        tourListView.setItems(viewModel.getTours()); // Touren laden
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // Mehrfachauswahl erlauben

        // Definiert, dass ListView nur den Namen der Tour anzeigt
        tourListView.setCellFactory(param -> new ListCell<Tour>() {
            @Override
            protected void updateItem(Tour tour, boolean empty) {
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });

        // TourLogs initialisieren
        tourLogListView.setItems(viewModel.getTourLogs());
        tourLogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TourLog defaultTourLog = new TourLog("TEST", "Wien-Graz", null, null, null, 0, 0, 0); // Leeres TourLog-Objekt
        viewModel.getTourLogs().add(defaultTourLog); // Füge es der Liste hinzu

        // Definiere das Layout der TourLog-Liste
        tourLogListView.setCellFactory(param -> new ListCell<TourLog>() {
            @Override
            protected void updateItem(TourLog tourLog, boolean empty) {
                super.updateItem(tourLog, empty);
                if (tourLog != null) {
                    setText(tourLog.getName());
                } else {
                    setText(null);
                }
            }
        });

        // Button-Mediator zum Aktivieren/Deaktivieren der Buttons
        new ButtonSelectionMediator(editButton, deleteButton, tourListView);

        // Lade die Tour-Detail-Ansicht
        loadTourDetailView();
        loadLogTourDetailView();
        // Wenn eine Tour ausgewählt wird, sollen die Details in der TourView angezeigt werden
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTour, newTour) -> {
            if (tourViewController != null) {
                tourLogViewController.setTourLog(null);
                tourViewController.setTour(newTour);
            }
        });

        // Listener für TourLog-Auswahl hinzufügen
        tourLogListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTourLog, newTourLog) -> {
            if (tourLogViewController != null) {
                tourViewController.setTour(null);
                tourLogViewController.setTourLog(newTourLog);
            }
        });

    }



    private void loadLogTourDetailView() { //Beim Klicken auf eine Tourlog
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogView.fxml"));
            Parent tourView = loader.load(); //Root der FXML laden
            tourLogViewController = loader.getController();


            tourDetailsContainer.getChildren().add(tourView); //Fügt TourView als child hinzu
            AnchorPane.setTopAnchor(tourView, 0.0);
            AnchorPane.setBottomAnchor(tourView, 0.0);
            AnchorPane.setLeftAnchor(tourView, 0.0);
            AnchorPane.setRightAnchor(tourView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTourDetailView() { //Beim Klicken auf eine Tour
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
            Parent tourView = loader.load(); //Root der FXML laden
            tourViewController = loader.getController();
            tourViewController.setViewModel(viewModel);

            tourDetailsContainer.getChildren().add(tourView); //Fügt TourView als child hinzu
            AnchorPane.setTopAnchor(tourView, 0.0);
            AnchorPane.setBottomAnchor(tourView, 0.0);
            AnchorPane.setLeftAnchor(tourView, 0.0);
            AnchorPane.setRightAnchor(tourView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void onCreateTour() { //Beim Klicken von Create
        try { //FXML von tourCreation laden
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
    public void onEditTour() { //Beim Klicken von Edit
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem(); //holt ausgewählte tour aus der Liste
        if (selectedTour == null) {
            return;
        }

        try { //FXML für TourEditController laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourEditView.fxml"));
            Parent root = loader.load();

            TourEditController controller = loader.getController();
            controller.setTour(selectedTour);

            controller.setOnTourUpdatedCallback(() -> {
                //Aktualisiere die ListView, um die Änderungen anzuzeigen:
                tourListView.refresh();
                //Hole die (aktualisierte) Tour aus der Auswahl:
                Tour updatedTour = tourListView.getSelectionModel().getSelectedItem();
                //Aktualisiere die Detailansicht (TourView) mit den neuen Daten:
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
    public void onDeleteTour() { //Klicken auf Delete
        var selectedTours = tourListView.getSelectionModel().getSelectedItems(); //holt alle ausgewählten Touren aus der ListView
        if (selectedTours.isEmpty()) {
            return;
        }

        //Erstellt einen Bestätigungs-Dialog, um versehentliches Löschen zu vermeiden:
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Tours");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) { //nur falls bestätigt, werden die ausgewählten Touren gelöscht
            viewModel.getTours().removeAll(selectedTours);
        }
    }

    @FXML
    public void onSelectTours() {
        setActiveView(true);
    }
    @FXML
    public void onSelectTourLogs() {
        setActiveView(false);
    }

    public void addTourLogToList(TourLog tourLog) {
        tourLogListView.getItems().add(tourLog);
    }


    private void setActiveView(boolean showTours) {
        // Sichtbarkeit der Listen anpassen
        tourListView.setVisible(showTours);
        tourLogListView.setVisible(!showTours);

        // Aktualisieren der Label-Stile, damit das aktive Label unterstrichen wird
        labelTours.setStyle(showTours
                ? "-fx-font-weight: bold; -fx-font-size: 16px; -fx-underline: true;"
                : "-fx-font-size: 16px; -fx-underline: false;");
        labelTourLogs.setStyle(!showTours
                ? "-fx-font-weight: bold; -fx-font-size: 16px; -fx-underline: true;"
                : "-fx-font-size: 16px; -fx-underline: false;");
    }

}
