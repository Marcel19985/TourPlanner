package org.example.tourplanner.ui.controllers;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.ui.viewmodels.TourViewModel;
import org.example.tourplanner.utils.HtmlTemplateLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class TourCreationController {

    @FXML private TextField tourNameField;
    @FXML private TextField tourDescriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;
    @FXML private WebView mapView;  // Das WebView für die Karte

    private WebEngine webEngine;
    private Consumer<Tour> onTourCreatedCallback;
    private Consumer<Tour> onTourUpdatedCallback;
    private static String API_KEY;
    // Hier speichern wir das Original-ViewModel und den Editing-Clone separat.
    private TourViewModel originalTourViewModel = null;
    private TourViewModel editingTourViewModel = null;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        //delete: wird verwendet um schneller auszufüllen
        tourNameField.setText("Test Tour");
        tourDescriptionField.setText("Test Beschreibung");
        startField.setText("Wien");
        destinationField.setText("Linz");
        transportTypeBox.setValue("Car");
        webEngine = mapView.getEngine();
        mapView.getEngine().setJavaScriptEnabled(true);


    }

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    public void setOnTourUpdatedCallback(Consumer<Tour> callback) {
        this.onTourUpdatedCallback = callback;
    }

    /**
     * Wird im Bearbeitungsmodus aufgerufen.
     * Es wird ein Editing-Clone des übergebenen TourViewModels erstellt,
     * an den dann die UI-Felder gebunden werden.
     */
    public void setTourForEditing(TourViewModel original) {
        this.originalTourViewModel = original;
        this.editingTourViewModel = new TourViewModel(original); // Editing-Clone erstellen

        // Bidirektionales Binding an den Editing-Clone
        tourNameField.textProperty().bindBidirectional(editingTourViewModel.nameProperty());
        tourDescriptionField.textProperty().bindBidirectional(editingTourViewModel.descriptionProperty());
        startField.textProperty().bindBidirectional(editingTourViewModel.startProperty());
        destinationField.textProperty().bindBidirectional(editingTourViewModel.destinationProperty());
        transportTypeBox.valueProperty().bindBidirectional(editingTourViewModel.transportTypeProperty());

        // Nur Name und Description sollen editierbar sein:
        startField.setDisable(true);
        destinationField.setDisable(true);
        transportTypeBox.setDisable(true);

        // Falls du Felder für distance/estimatedTime hast, diese ebenfalls deaktivieren:
        // distanceField.setDisable(true);
        // estimatedTimeField.setDisable(true);
    }

    @FXML
    private void onSaveButtonClick(javafx.event.ActionEvent event) {
        // Überprüfe, ob die Eingaben gültig sind
        if (!InputValidator.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        // Stage aus dem Event abrufen
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (editingTourViewModel != null) {
            // Bearbeitungsmodus: Änderungen übernehmen
            originalTourViewModel.copyFrom(editingTourViewModel);
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(originalTourViewModel.getTour());
            }

        } else {
            // Erstellungsmodus: Route berechnen
            try {
                String start = startField.getText();
                String destination = destinationField.getText();
                String transportType = transportTypeBox.getValue();

                // Abruf der Routendetails via OpenRouteServiceClient
                double[] routeDetails = OpenRouteServiceClient.getRouteDetails(start, destination, transportType);
                double distance = routeDetails[0];
                double estimatedTime = routeDetails[1];

                // Lade die Karte mit den Koordinaten
                loadMap(start, destination);

                // Erzeuge die neue Tour
                Tour newTour = new Tour(
                        tourNameField.getText(),
                        tourDescriptionField.getText(),
                        start,
                        destination,
                        transportType,
                        distance,
                        estimatedTime
                );

                // Screenshot machen und danach Fenster schließen
                takeMapScreenshot(newTour, currentStage);

                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(newTour);
                }

            } catch (Exception e) {
                showAlert("An error occurred while retrieving route information: " + e.getMessage());
            }
        }
    }


    private void loadMap(String start, String destination) {
        try {
            // Hole die Koordinaten des Start- und Zielorts über OpenRouteServiceClient
            String startCoords = OpenRouteServiceClient.getCoordinates(start);
            String destCoords = OpenRouteServiceClient.getCoordinates(destination);

            // Erstelle das HTML für die Karte
            String html = HtmlTemplateLoader.loadTourMapHtml(startCoords, destCoords, transportTypeBox.getValue());

            // Lade das HTML in den WebView
            webEngine.loadContent(html);

        } catch (Exception e) {
            showAlert("Error while loading map: " + e.getMessage());
        }
    }


    private void takeMapScreenshot(Tour tour, Stage stageToClose) {
        // Überprüfen, ob das WebView sichtbar ist
        if (!mapView.isVisible()) {
            System.err.println("Fehler: mapView ist nicht sichtbar!");
            return;
        }

        // Warte einige Frames, damit WebView sicher gerendert wird
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            System.out.println("Erstelle Screenshot...");

            WritableImage image = mapView.snapshot(new SnapshotParameters(), null);

            BufferedImage bufferedImage = new BufferedImage(
                    (int) image.getWidth(),
                    (int) image.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    bufferedImage.setRGB(x, y, image.getPixelReader().getArgb(x, y));
                }
            }

            File dir = new File("target/images");
            if (!dir.exists() && !dir.mkdirs()) {
                System.err.println("Fehler beim Erstellen des Verzeichnisses 'target/images'.");
                return;
            }

            File file = new File(dir, tour.getId() + ".png");
            try {
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Screenshot gespeichert: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Fehler beim Speichern des Screenshots: " + e.getMessage());
            }

            // 🔥 Fenster erst jetzt schließen!
            System.out.println("Schließe Fenster...");
            stageToClose.close();
        });

        delay.play();
    }






    @FXML
    private void onCancelButtonClick() {
        // Beim Cancel wird der Editing-Clone verworfen und das Original bleibt unverändert.
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tourNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
