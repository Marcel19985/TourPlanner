package org.example.tourplanner.presentationLayer.controllers;



import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.tourplanner.businessLayer.services.OpenRouteServiceClient;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.services.TourService;
import org.example.tourplanner.presentationLayer.viewmodels.TourViewModel;
import org.example.tourplanner.utils.HtmlTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Controller
@Scope("prototype") //wenn TourCreationController von Spring angefordert wird, wird jedes mal eine neue Instanz erstellt (falls mehrere Create oder Edit Fenster gleichzeitig offen sind
public class TourCreationController {

    @FXML private TextField tourNameField;
    @FXML private TextField tourDescriptionField;
    @FXML private TextField startField;
    @FXML private TextField destinationField;
    @FXML private ComboBox<String> transportTypeBox;
    @FXML private WebView mapView; //WebView für die Karte
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

    private static final Logger logger = LogManager.getLogger(TourCreationController.class);

    @Autowired //automatischer Dependency Injection -> Spring sucht nach einer Instanz von TourService und injiziert sie hier (gibt nur eine Instanz davon)
    private TourService tourService;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike");
        //Füllwerte (nur für Testzwecke): delete before submission!
        tourNameField.setText("Test Tour");
        tourDescriptionField.setText("Test Beschreibung");
        startField.setText("Wien");
        destinationField.setText("Linz");
        transportTypeBox.setValue("Car");

        webEngine = mapView.getEngine();
        mapView.getEngine().setJavaScriptEnabled(true);

        //Falls Start oder Destination nach Laden der Karte geändert werden, deaktiviere den Save-Button:
        startField.textProperty().addListener((observable, oldValue, newValue) -> checkForRouteChange());
        destinationField.textProperty().addListener((observable, oldValue, newValue) -> checkForRouteChange());

        // Tastenkürzel hinzufügen
        tourNameField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getAccelerators().put(
                        new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.L, javafx.scene.input.KeyCombination.CONTROL_DOWN),
                        this::onLoadMapClick
                );
                newScene.getAccelerators().put(
                        new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.S, javafx.scene.input.KeyCombination.CONTROL_DOWN),
                        () -> saveButton.fire()
                );
                newScene.getAccelerators().put(
                        new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.C, javafx.scene.input.KeyCombination.CONTROL_DOWN),
                        this::onCancelButtonClick
                );
                newScene.getAccelerators().put(
                        new javafx.scene.input.KeyCodeCombination(javafx.scene.input.KeyCode.ESCAPE),
                        this::onCancelButtonClick
                );
            }
        });
    }

    public void setOnTourCreatedCallback(Consumer<Tour> callback) {
        this.onTourCreatedCallback = callback;
    }

    public void setOnTourUpdatedCallback(Consumer<Tour> callback) {
        this.onTourUpdatedCallback = callback;
    }

    //Wird im Bearbeitungsmodus aufgerufen. Es wird ein Editing-Clone des übergebenen TourViewModels erstellt, an den dann die UI-Felder gebunden werden:
    public void setTourForEditing(TourViewModel original) {
        this.originalTourViewModel = original;
        this.editingTourViewModel = new TourViewModel(original); //Editing-Clone erstellen

        //Bidirektionales Binding an den Editing-Clone:
        tourNameField.textProperty().bindBidirectional(editingTourViewModel.nameProperty());
        tourDescriptionField.textProperty().bindBidirectional(editingTourViewModel.descriptionProperty());
        startField.textProperty().bindBidirectional(editingTourViewModel.startProperty());
        destinationField.textProperty().bindBidirectional(editingTourViewModel.destinationProperty());
        transportTypeBox.valueProperty().bindBidirectional(editingTourViewModel.transportTypeProperty());

        //Nur Name und Description sollen editierbar sein:
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
        //Überprüfe die Eingaben:
        if (!InputValidator.validateTourInputs(tourNameField, tourDescriptionField, startField, destinationField, transportTypeBox)) {
            return;
        }

        //Stage aus dem Event abrufen:
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (editingTourViewModel != null) {
            //Bearbeitungsmodus: Änderungen übernehmen
            originalTourViewModel.copyFrom(editingTourViewModel);
            //Persistiere die geänderte Tour
            Tour updatedTour = tourService.saveTour(originalTourViewModel.getTour());
            if (onTourUpdatedCallback != null) {
                onTourUpdatedCallback.accept(updatedTour);
            }
            currentStage.close();
        } else {
            //Erstellungsmodus: Route berechnen
            try {
                Tour newTour = new Tour(
                        tourNameField.getText(),
                        tourDescriptionField.getText(),
                        startField.getText(),
                        destinationField.getText(),
                        transportTypeBox.getValue()
                );

                //Abruf der Routendetails via OpenRouteServiceClient:
                newTour = OpenRouteServiceClient.getRouteDetails(newTour); //todo: eventuell nur Tour Objekt übergeben DONE

                takeMapScreenshot(newTour, currentStage); //Screenshot erstellen

                //Persistiere die neue Tour in der Datenbank:
                Tour savedTour = tourService.saveTour(newTour);

                if (onTourCreatedCallback != null) {
                    onTourCreatedCallback.accept(savedTour);
                }

            } catch (Exception e) {
                logger.error("Error while creating new Tour (OpenRouteService failed or saving)", e);
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
                System.err.println("Coordinated could not be loaded.");
                logger.error("Coordinates could not be loaded for start or destination.");
                saveButton.setDisable(true);
                return;
            }

            String html = HtmlTemplateLoader.loadTourMapHtml(startCoords, destCoords, transportTypeBox.getValue().toString());
            mapView.getEngine().loadContent(html);

            //Warte, bis die Karte geladen ist, bevor "Save" aktiviert wird:
            mapView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    isMapLoaded = true;
                    saveButton.setDisable(false);
                }
            });

        } catch (Exception e) {
            showAlert("An error occurred while retrieving route information: " + e.getMessage());
            logger.error("An error occurred while retrieving route information: ", e);
        }
    }

    private void checkForRouteChange() {
        //Wenn die Karte geladen ist und der Benutzer Änderungen vornimmt, deaktiviere den Save-Button:
        if (isMapLoaded) {
            saveButton.setDisable(true);
        }
    }

    //Mithilfe von ChatGPT generiert:
    private void takeMapScreenshot(Tour tour, Stage stageToClose) {
        if (!mapView.isVisible()) {
            System.err.println("Error: mapView is not visible!");
            return;
        }

        System.out.println("Create Screenshot...");

        WritableImage image = mapView.snapshot(new SnapshotParameters(), null); //Erstelle einen Snapshot der Karte
        BufferedImage bufferedImage = new BufferedImage(
                (int) image.getWidth(),
                (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        //Pixel werden auf BufferedImage kopiert:
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                bufferedImage.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }

        //Verzeichnis target/images erstellen falls nicht existent:
        File dir = new File("target/images");
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Error while creating directory 'target/images'.");
            logger.error("Error while creating directory 'target/images'.");
            return;
        }

        File file = new File(dir, tour.getId() + ".png"); //Bild wird als png mit Tour-ID gespeichert
        try {
            ImageIO.write(bufferedImage, "png", file);
            System.out.println("Screenshot saved: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while saving Screenshot: " + e.getMessage());
            logger.error("Error while saving Screenshot: ", e);
        }

        // Fenster schließen
        System.out.println("Close Window...");
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
