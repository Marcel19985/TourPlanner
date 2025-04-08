package org.example.tourplanner.presentationLayer.UiTests;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;

public class TourCreationUITest extends ApplicationTest {

    private Stage primaryStage;

    @BeforeEach
    public void setUpTour() throws Exception {
        // Überprüfe, ob bereits eine Tour vorhanden ist.
        // Falls nicht, erstelle eine neue Tour, die als Basis für Tour Log Tests dient.
        // Hier simulieren wir die Erstellung einer Tour:
        clickOn("#createButton");
        clickOn("#tourNameField").write("BeforeEach");
        clickOn("#tourDescriptionField").write("This is a test tour");
        clickOn("#startField").write("Wien");
        clickOn("#destinationField").write("Linz");
        clickOn("#transportTypeBox");
        clickOn("Car");
        clickOn("Save");
        // Warte, bis die UI aktualisiert ist.
        WaitForAsyncUtils.waitForFxEvents();
    }


    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/tourplanner/MainView.fxml"));
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/org/example/tourplanner/stylesheet.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @Test
    public void testCreateTour() throws Exception {
        //Klicke auf den "Create"-Button in der MainView
        clickOn("#createButton");

        //Nun sollte ein neues Fenster/Dialog erscheinen (TourCreationView)
        clickOn("#tourNameField").write("Tour Test");
        clickOn("#tourDescriptionField").write("Diese Tour ist eine Test Tour");
        clickOn("#startField").write("Wien");
        clickOn("#destinationField").write("Linz");
        clickOn("#transportTypeBox");
        clickOn("Car");

        //Klicke auf den "Save"-Button
        clickOn("Save");

        //überprüfen, ob die neue Tour "My New Tour" in der ListView ist
        verifyThat("#tourListView", (ListView<Tour> lv) ->
                lv.getItems().stream().anyMatch(tour -> tour.toString().contains("Tour Test - Wien → Linz (Car)"))
        );
    }

    @Test
    public void testCreateTourLog() throws Exception {
        //Ensure a tour is selected
        clickOn("#tourListView");
        // Click on an item in the tourListView. Here we assume that the tour's toString() includes "Wien".
        clickOn("BeforeEach");

        //2. Switch to the "Tour Logs" tab.
        clickOn("Tour Logs");

        //3. Click on the Create button
        clickOn("#createButton");

        //fill out the fields.
        clickOn("#nameLog").write("Test Log");
        clickOn("#datePicker");
        write("20.05.2022");
        clickOn("#difficultyComboBox");
        clickOn("Medium");
        clickOn("#totalDistanceField").write("50");
        clickOn("#totalTimeField").write("60");
        clickOn("#ratingComboBox");
        clickOn("5");
        clickOn("#commentField").write("This is a test log.");

        //5. Click on the Save button
        clickOn("Save");

        verifyThat("#tourLogListView", (ListView<TourLog> lv) ->
                lv.getItems().stream().anyMatch(tour -> tour.toString().contains("Test Log"))
        );
    }
}