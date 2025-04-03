package org.example.tourplanner.ui.controllers;



import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.OpenRouteServiceClient;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.repositories.TourRepository;
import org.example.tourplanner.ui.viewmodels.TourViewModel;
import org.example.tourplanner.utils.HtmlTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Controller
@Scope("prototype") //jedes Mal ein neuer Controller vom Spring-Kontext erzeugt wird -> damit create nach edit funktioniert
public class TourCreationController {

    @FXML private TextField tourNameField;
    @FXML private TextField tourDescriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;
    @FXML private WebView mapView;  // Das WebView für die Karte
    @FXML private Button saveButton;
    @FXML private Button loadMapButton;
    @FXML private Label screenshotInfoLabel;

    private WebEngine webEngine;
    private Consumer<Tour> onTourCreatedCallback;
    private Consumer<Tour> onTourUpdatedCallback;
    private boolean isMapLoaded = false;

    // Original-ViewModel und Editing-Clone im Bearbeitungsmodus
    private TourViewModel originalTourViewModel = null;
    private TourViewModel editingTourViewModel = null;

    // Repository zur Persistierung (wird per Spring injiziert)
    @Autowired
    private TourRepository tourRepository;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        // Füllwerte (nur für Testzwecke)
        tourNameField.setText("Test Tour");
        tourDescriptionField.setText("Test Beschreibung");
        startField.setText("Wien");
        destinationField.setText("Linz");
        transportTypeBox.setValue("Car");

        webEngine = mapView.getEngine();
        mapView.getEngine().setJavaScriptEnabled(true);

        // Falls Start oder Destination nach Laden der Karte geändert werden, deaktiviere den Save-Button
        startField.textProperty().addListener((observable, oldValue, newValue) -> checkForRouteChange());
        destinationField.textProperty().addListener((observable, oldValue, newValue) -> checkForRouteChange());
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

        // Nur Name und Description sollen editierbar sein
        startField.setDisable(true);
        destinationField.setDisable(true);
        transportTypeBox.setDisable(true);
        mapView.setVisible(false);
        loadMapButton.setVisible(false);
        screenshotInfoLabel.setVisible(false);
        saveButton.setDisable(false);
    }

    @FXML
    private void onSaveButtonClick(javafx.event.ActionEvent event) {
        // Überprüfe die Eingaben
        if (!InputValidator.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        // Stage aus dem Event abrufen
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (editingTourViewModel != null) {
            // Bearbeitungsmodus: Änderungen übernehmen
            originalTourViewModel.copyFrom(editingTourViewModel);
            // Persistiere die geänderte Tour
            Tour updatedTour = tourRepository.save(originalTourViewModel.getTour());
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(updatedTour);
            }
            currentStage.close();
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

                // Screenshot erstellen und Fenster anschließend schließen
                takeMapScreenshot(newTour, currentStage);

                // Persistiere die neue Tour in der Datenbank
                Tour savedTour = tourRepository.save(newTour);
                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(savedTour);
                }

            } catch (Exception e) {
                showAlert("An error occurred while retrieving route information: " + e.getMessage());
            }
        }
        isMapLoaded = false;
    }

    @FXML
    private void onLoadMapClick() {
        try {
            String startCoords = OpenRouteServiceClient.getCoordinates(startField.getText());
            String destCoords = OpenRouteServiceClient.getCoordinates(destinationField.getText());

            if (startCoords == null || destCoords == null) {
                System.err.println("Fehler: Konnte die Koordinaten nicht abrufen.");
                saveButton.setDisable(true);
                return;
            }

            String html = HtmlTemplateLoader.loadTourMapHtml(startCoords, destCoords, transportTypeBox.getValue().toString());
            mapView.getEngine().loadContent(html);

            // Warte, bis die Karte geladen ist, bevor "Save" aktiviert wird
            mapView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    isMapLoaded = true;
                    saveButton.setDisable(false);
                }
            });

        } catch (Exception e) {
            showAlert("An error occurred while retrieving route information: " + e.getMessage());
        }
    }

    private void checkForRouteChange() {
        // Wenn die Karte geladen ist und der Benutzer Änderungen vornimmt, deaktivieren wir den Save-Button
        if (isMapLoaded) {
            saveButton.setDisable(true);
        }
    }

    private void takeMapScreenshot(Tour tour, Stage stageToClose) {
        if (!mapView.isVisible()) {
            System.err.println("Fehler: mapView ist nicht sichtbar!");
            return;
        }

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

        // Fenster schließen
        System.out.println("Schließe Fenster...");
        stageToClose.close();
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
