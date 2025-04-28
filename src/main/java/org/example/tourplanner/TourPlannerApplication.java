package org.example.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.tourplanner.helpers.SpringContext;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

//todo: tag in git erstellen vor intermediate hand in
public class TourPlannerApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Starte den Spring-Kontext (hier verwendet SpringBootMain als Konfigurationsklasse)
        springContext = new SpringApplicationBuilder(SpringBootMain.class).run();
        // Speichere den Kontext in einer Hilfsklasse, damit du ihn in Controllern abrufen kannst
        SpringContext.setApplicationContext(springContext);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Lade FXML und verwende den Spring-Kontext, um Controller zu erzeugen
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/MainView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("/org/example/tourplanner/stylesheet.css").toExternalForm());
        stage.setTitle("TourPlanner");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
