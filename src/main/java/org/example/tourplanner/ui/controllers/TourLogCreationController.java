package org.example.tourplanner.ui.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.repositories.TourLogRepository;
import org.example.tourplanner.ui.viewmodels.TourLogViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Controller
@Scope("prototype")
public class TourLogCreationController {

    @FXML private TextField nameLog;
    @FXML private TextArea commentField;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField totalDistanceField;
    @FXML private TextField totalTimeField;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    private Tour currentTour;
    private Consumer<TourLog> onTourLogCreatedCallback;
    private Consumer<TourLog> onTourLogUpdatedCallback;

    // Original und Editing-Clone im Bearbeitungsmodus
    private TourLogViewModel originalTourLogViewModel = null;
    private TourLogViewModel editingTourLogViewModel = null;

    // Repository zur Persistierung, via Spring injiziert
    @Autowired
    private TourLogRepository tourLogRepository;

    @FXML
    private void initialize() {
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        // Testwerte
        nameLog.setText("Test Log");
        commentField.setText("Test Comment");
        difficultyComboBox.setValue("Easy");
        totalDistanceField.setText("20");
        totalTimeField.setText("20");
        ratingComboBox.setValue(5);
        datePicker.setValue(LocalDate.now());
    }

    public void setCurrentTour(Tour currentTour) {
        this.currentTour = currentTour;
    }

    /**
     * Im Bearbeitungsmodus übergeben wir das bereits vorhandene ViewModel.
     * Es wird ein Editing-Clone erstellt, an den die UI-Felder gebunden werden.
     */
    public void setTourLogForEditing(TourLogViewModel original) {
        this.originalTourLogViewModel = original;
        this.editingTourLogViewModel = new TourLogViewModel(original); // Clone erstellen
        nameLog.textProperty().bindBidirectional(editingTourLogViewModel.nameProperty());
        commentField.textProperty().bindBidirectional(editingTourLogViewModel.commentProperty());
        difficultyComboBox.valueProperty().bindBidirectional(editingTourLogViewModel.difficultyProperty());
        totalDistanceField.textProperty().bindBidirectional(editingTourLogViewModel.totalDistanceProperty(), new NumberStringConverter());
        totalTimeField.textProperty().bindBidirectional(editingTourLogViewModel.totalTimeProperty(), new NumberStringConverter());
        ratingComboBox.valueProperty().bindBidirectional(editingTourLogViewModel.ratingProperty().asObject());
        datePicker.valueProperty().bindBidirectional(editingTourLogViewModel.dateProperty());
        IntegerProperty hour = new SimpleIntegerProperty();
        IntegerProperty minute = new SimpleIntegerProperty();

        hourSpinner.getValueFactory().valueProperty().bindBidirectional(hour.asObject());
        minuteSpinner.getValueFactory().valueProperty().bindBidirectional(minute.asObject());

        editingTourLogViewModel.timeProperty().bind(Bindings.createObjectBinding(
                () -> LocalTime.of(hour.get(), minute.get()),
                hour, minute
        ));
    }

    public void setOnTourLogCreatedCallback(Consumer<TourLog> callback) {
        this.onTourLogCreatedCallback = callback;
    }

    public void setOnTourLogUpdatedCallback(Consumer<TourLog> callback) {
        this.onTourLogUpdatedCallback = callback;
    }

    @FXML
    private void onSaveTourLog() {
        if (!InputValidator.validateTourLogInputs(nameLog, datePicker, commentField, difficultyComboBox, totalDistanceField, totalTimeField, ratingComboBox, minuteSpinner, hourSpinner)) {
            return;
        }
        try {
            TourLog tourLog;
            if (editingTourLogViewModel != null) {
                // Bearbeitungsmodus: Änderungen übernehmen
                originalTourLogViewModel.copyFrom(editingTourLogViewModel);
                // Persistiere das aktualisierte TourLog
                tourLog = tourLogRepository.save(originalTourLogViewModel.getTourLog());
                if (onTourLogUpdatedCallback != null) {
                    onTourLogUpdatedCallback.accept(tourLog);
                }
            } else {
                // Erstellungsmodus: Neues TourLog erzeugen
                String name = nameLog.getText();
                String comment = commentField.getText();
                String difficulty = difficultyComboBox.getValue();
                double totalDistance = Double.parseDouble(totalDistanceField.getText());
                double totalTime = Double.parseDouble(totalTimeField.getText());
                int rating = ratingComboBox.getValue();
                LocalDate date = datePicker.getValue();
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();
                LocalTime time = LocalTime.of(hour, minute);

                tourLog = new TourLog(name, date, time, comment, difficulty, totalDistance, totalTime, rating);
                // Setze ggf. den Tournamen (oder direkt die Beziehung zur Tour, falls deine Entity dies unterstützt)
                if (currentTour != null) {
                    // Falls in deiner TourLog-Entity keine direkte Beziehung zur Tour gesetzt wird, kannst du hier den Namen speichern.
                    // Bei Beziehung würde man z. B. tourLog.setTour(currentTour) aufrufen.
                    tourLog.setTour(currentTour);
                }
                // Persistiere das neue TourLog
                tourLog = tourLogRepository.save(tourLog);
                if (onTourLogCreatedCallback != null) {
                    onTourLogCreatedCallback.accept(tourLog);
                }
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Invalid input for distance or time.");
        }
    }

    @FXML
    private void onCancel() {
        if (editingTourLogViewModel != null) { // Unbind, falls im Bearbeitungsmodus
            unbindFields();
        }
        closeWindow();
    }

    private void unbindFields() {
        nameLog.textProperty().unbindBidirectional(editingTourLogViewModel.nameProperty());
        commentField.textProperty().unbindBidirectional(editingTourLogViewModel.commentProperty());
        difficultyComboBox.valueProperty().unbindBidirectional(editingTourLogViewModel.difficultyProperty());
        totalDistanceField.textProperty().unbindBidirectional(editingTourLogViewModel.totalDistanceProperty());
        totalTimeField.textProperty().unbindBidirectional(editingTourLogViewModel.totalTimeProperty());
        ratingComboBox.valueProperty().unbindBidirectional(editingTourLogViewModel.ratingProperty().asObject());
        datePicker.valueProperty().unbindBidirectional(editingTourLogViewModel.dateProperty());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameLog.getScene().getWindow();
        stage.close();
    }
}
