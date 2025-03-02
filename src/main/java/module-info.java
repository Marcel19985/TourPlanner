module org.example.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires json;
    requires org.json;

    opens org.example.tourplanner to javafx.fxml;
    exports org.example.tourplanner;

    exports org.example.tourplanner.data.database;
    opens org.example.tourplanner.data.database to javafx.fxml;
    exports org.example.tourplanner.data.models;
    opens org.example.tourplanner.data.models to javafx.fxml;

    exports org.example.tourplanner.ui.controllers;
    opens org.example.tourplanner.ui.controllers to javafx.fxml;
    exports org.example.tourplanner.ui.viewmodels;
    opens org.example.tourplanner.ui.viewmodels to javafx.fxml;

}
