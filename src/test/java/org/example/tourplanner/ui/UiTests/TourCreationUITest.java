package org.example.tourplanner.ui.UiTests;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.tourplanner.data.models.Tour;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;

public class TourCreationUITest extends ApplicationTest {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/tourplanner/MainView.fxml"));
        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @Test
    public void testCreateTour() throws Exception {
        // Klicke auf den "Create"-Button in der MainView
        clickOn("#createButton");

        // Nun sollte ein neues Fenster/Dialog erscheinen (TourCreationView)
        clickOn("#tourNameField").write("Tour Test");
        clickOn("#tourDescriptionField").write("Diese Tour ist eine Test Tour");
        clickOn("#startField").write("Wien");
        clickOn("#destinationField").write("Linz");
        clickOn("#transportTypeBox");
        clickOn("Car");

        // Klicke auf den "Save"-Button
        clickOn("Save");

        //überprüfen, ob die neue Tour "My New Tour" in der ListView ist
        verifyThat("#tourListView", (ListView<Tour> lv) ->
                lv.getItems().stream().anyMatch(tour -> tour.toString().contains("Tour Test - Wien → Linz (Car)"))
        );
    }
}