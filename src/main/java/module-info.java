module org.example.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.tourplanner to javafx.fxml;
    exports org.example.tourplanner;

    exports org.example.tourplanner.data.database;
    opens org.example.tourplanner.data.database to javafx.fxml;
    exports org.example.tourplanner.data.models;
    opens org.example.tourplanner.data.models to javafx.fxml;

    exports org.example.tourplanner.ui.viewmodels;
    opens org.example.tourplanner.ui.viewmodels to javafx.fxml;

}
