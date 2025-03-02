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
    private ListView<Tour> tourListView; //Liste aller Tournamen

    @FXML //Detailansicht von ausgewählter Tour
    private AnchorPane tourDetailsContainer;

    private final MainViewModel viewModel = new MainViewModel();

    //Referenz auf den TourViewController
    private TourViewController tourViewController;

    @FXML
    private void initialize() { //automatisch vom FXML loader aufgerufen
        tourListView.setItems(viewModel.getTours()); //Touren laden
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Mehrfachauswahl in der Liste erlaubt

        //Definiert, dass ListView nur den Namen der Tour anzeigt:
        tourListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour tour, boolean empty) {
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });

        //Button-Mediator zum Aktivieren/Deaktivieren der Buttons:
        new ButtonSelectionMediator(editButton, deleteButton, tourListView);

        //Lade die Tour-Detail-Ansicht und speichere den Controller:
        loadTourDetailView();

        //Wenn eine Tour ausgewählt wird, sollen die Details in der TourView angezeigt werden:
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTour, newTour) -> {
            if (tourViewController != null) {
                tourViewController.setTour(newTour);
            }
        });
    }

    private void loadTourDetailView() { //Beim Klicken auf eine Tour
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
            Parent tourView = loader.load(); //Root der FXML laden
            tourViewController = loader.getController();
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
            controller.setOnTourUpdatedCallback(() -> tourListView.refresh());

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
}
