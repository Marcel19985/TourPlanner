package org.example.tourplanner.presentationLayer.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.businessLayer.services.ImportExportService;
import org.example.tourplanner.businessLayer.services.ReportService;
import org.example.tourplanner.helpers.SpringContext;
import org.example.tourplanner.presentationLayer.mediators.ButtonSelectionMediator;
import org.example.tourplanner.businessLayer.services.TourService;
import org.example.tourplanner.businessLayer.services.TourLogService;
import org.example.tourplanner.presentationLayer.viewmodels.MainViewModel;
import org.example.tourplanner.presentationLayer.viewmodels.TourViewModel;
import org.example.tourplanner.presentationLayer.viewmodels.TourLogViewModel;
import javafx.collections.FXCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainViewController {

    @FXML private BorderPane mainPane;
    @FXML private ListView<TourViewModel> tourListView; //Liste mit TourViewModel
    @FXML private TabPane detailTabPane; //Auswahl zwischen Tour und TourLog Anzeige
    @FXML private AnchorPane tourDetailsContainer;
    @FXML private AnchorPane tourLogDetailsContainer;
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    //Für Suche von Tours:
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button clearSearchButton;
    @FXML private CheckBox    childFriendlyCheckbox;
    @FXML private Spinner<Double> minPopularitySpinner;

    private MainViewModel viewModel = new MainViewModel();

    private TourViewController tourViewController;
    private TourLogViewController tourLogViewController;

    private ButtonSelectionMediator<TourViewModel> tourMediator;
    private ButtonSelectionMediator<TourLogViewModel> tourLogMediator;

    @Autowired //autowired = es gibt in gesamter Spring application nur eine Instanz von TourService; Gegenteil = prototype
    private TourService tourService;
    @Autowired
    private TourLogService tourLogService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ImportExportService importExportService;
    @FXML
    private void initialize() {
        // Binde die Liste an die ViewModel-Liste
        tourListView.setItems(viewModel.getTourViewModels());
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tourListView.setCellFactory(param -> new ListCell<TourViewModel>() { //wird für jede Zelle in der Liste aufgerufen
            @Override
            protected void updateItem(TourViewModel tvm, boolean empty) { //wird aufgerufen, sobald ein Element in der Liste aktualisiert/geladen wird
                super.updateItem(tvm, empty);
                setText(empty || tvm == null ? null : tvm.nameProperty().get());
            }
        });

        // Lade Detail-Views
        loadTourDetailView();
        loadTourLogDetailView();

        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTVM, newTVM) -> { //Listener für TourList
            if (newTVM != null) { //neu ausgewähltes Element
                tourViewController.setTour(newTVM.getTour()); //Details der Tour werden angezeigt
                // Tour Log details laden:
                tourLogViewController.setTourLogItems(
                        newTVM.getTour().getId(),
                        newTVM.getTourLogViewModels()
                );

                tourLogViewController.setTour(newTVM.getTour().getId()); //neu: nur noch die ID übergeben, lädt alles frisch
            } else { //Wenn keine Elemente ausgewählt -> setze Tours und TourLogs auf null
                tourViewController.setTour(null);
                tourLogViewController.clearDetails();
            }
        });

        tourMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourListView);
        tourLogMediator = new ButtonSelectionMediator<>(editButton, deleteButton, tourLogViewController.getTourLogListView());

        detailTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> { //Je nachdem ob Tour oder TourLog im tabpane ausgewählt ist
            if (newTab != null) {
                if (newTab.getText().equals("Tour Details")) { //TourDetails ausgewählt -> Button Texts von Create, Edit und Delete werden aktualisiert
                    createButton.setText("_Create Tour");
                    editButton.setText("_Edit Tour");
                    deleteButton.setText("_Delete Tour(s)");
                    tourMediator.enable();
                    tourLogMediator.disable();
                } else if (newTab.getText().equals("Tour Logs")) {
                    createButton.setText("_Create Tour Log");
                    editButton.setText("_Edit Tour Log");
                    deleteButton.setText("_Delete Tour Log(s)");
                    tourLogMediator.enable();
                    tourMediator.disable();
                }
            }
        });

        mainPane.setOnMouseClicked(event -> { //Bei Klick außerhalb der Liste, wird keine Tour mehr angezeigt
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (clickedNode != null && !tourListView.equals(clickedNode)) {
                tourListView.getSelectionModel().clearSelection();
            }
        });

        mainPane.sceneProperty().addListener((obs, oldScene, newScene) -> { //Shortcut: mit Strg + char kann man Button auswählen
            if (newScene != null) {
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN),
                        this::onCreate
                );
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN),
                        this::onEdit
                );
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN),
                        this::onDelete
                );
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.DELETE),
                        this::onDelete
                );
            }
        });

        viewModel.getTourViewModels().setAll(
                tourService.getAllTours() //Lädt alle Touren aus der DB
                        .stream()
                        .distinct()  //Duplikate entfernen
                        .map(TourViewModel::new)
                        .toList()
        );

        SpinnerValueFactory<Double> factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10, 0, 1);
        minPopularitySpinner.setValueFactory(factory);
        loadAllTours();

    } //Initialize end

    @FXML
    private void onSearch() {
        String term = searchField.getText().trim();
        double minPop = minPopularitySpinner.getValue();
        boolean onlyChild = childFriendlyCheckbox.isSelected();

        List<Tour> found = tourService.searchTours(term, minPop, onlyChild);
        viewModel.getTourViewModels().setAll(
                found.stream().map(TourViewModel::new).toList()
        );
    }

    @FXML
    private void onClearSearch() {
        searchField.clear();
        childFriendlyCheckbox.setSelected(false);
        minPopularitySpinner.getValueFactory().setValue(0.0);
        loadAllTours();
    }

    private void loadAllTours() {
        viewModel.getTourViewModels().setAll(
                tourService.getAllTours().stream()
                        .distinct()
                        .map(TourViewModel::new)
                        .toList()
        );
    }



    private void loadTourDetailView() { //in initialize() aufgerufen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourView.fxml"));
            Parent tourView = loader.load();
            tourViewController = loader.getController();
            tourViewController.setViewModel(viewModel);
            tourDetailsContainer.getChildren().add(tourView);
            AnchorPane.setTopAnchor(tourView, 0.0);
            AnchorPane.setBottomAnchor(tourView, 0.0);
            AnchorPane.setLeftAnchor(tourView, 0.0);
            AnchorPane.setRightAnchor(tourView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTourLogDetailView() { //in initialize() aufgerufen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogSplitView.fxml"));
            // Controller von Spring holen, damit @Autowired funktioniert
            loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz));
            Parent tourLogSplitView = loader.load();
            tourLogViewController = loader.getController();
            tourLogDetailsContainer.getChildren().add(tourLogSplitView);
            AnchorPane.setTopAnchor(tourLogSplitView, 0.0);
            AnchorPane.setBottomAnchor(tourLogSplitView, 0.0);
            AnchorPane.setLeftAnchor(tourLogSplitView, 0.0);
            AnchorPane.setRightAnchor(tourLogSplitView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreate() { //Create reagiert je nach Auswahl auf Tour oder TourLog
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.getText().equals("Tour Details")) {
            onCreateTour();
        } else if (selectedTab.getText().equals("Tour Logs")) {
            onCreateTourLog();
        }
    }

    private void onCreateTour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
            loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz));
            Parent root = loader.load();
            TourCreationController controller = loader.getController();

            controller.setOnTourCreatedCallback(newTour -> { //Tour wird in viewModel gespeichert
                viewModel.addTour(newTour);
            });
            Stage stage = new Stage();
            stage.setTitle("Create New Tour");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCreateTourLog() {
        TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTVM == null) { //keine Tour ausgewählt
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a tour before creating a tour log.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
            // Wichtig: Controller über den Spring-Kontext erzeugen
            loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz));
            Parent root = loader.load();
            TourLogCreationController controller = loader.getController();
            controller.setCurrentTour(selectedTVM.getTour());
            controller.setOnTourLogCreatedCallback(tourLog -> {
                //1. ViewModel updaten, damit TourView/List korrekt bleibt
                selectedTVM.addTourLog(tourLog);

                //2. **Nur** einmal komplett neu laden
                tourLogViewController.setTour(selectedTVM.getTour().getId());
            });
            Stage stage = new Stage();
            stage.setTitle("Create Tour Log");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onEdit() {
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getText().equals("Tour Details")) {
            TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTVM == null) { //keine Tour ausgewählt
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourCreationView.fxml"));
                loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz)); //Spring Beans in JavaFX integriert
                Parent root = loader.load();
                TourCreationController controller = loader.getController();
                //Übergib das vorhandene TourViewModel zum Bearbeiten
                controller.setTourForEditing(selectedTVM);
                controller.setOnTourUpdatedCallback(updatedTour -> {
                    tourListView.refresh();
                    tourViewController.setTour(selectedTVM.getTour());
                });
                Stage stage = new Stage();
                stage.setTitle("Edit Tour");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectedTab != null && selectedTab.getText().equals("Tour Logs")) {
            TourLogViewModel selectedLogVM = tourLogViewController.getSelectedTourLogViewModel();
            if (selectedLogVM == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Log Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour log to edit.");
                alert.showAndWait();
                return;
            }
            try {
                TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/tourplanner/TourLogCreationView.fxml"));
                // Verwende die ControllerFactory, um den Controller als Spring Bean zu erzeugen
                loader.setControllerFactory(clazz -> SpringContext.getApplicationContext().getBean(clazz)); //Spring Beans in JavaFX integriert
                Parent root = loader.load();
                TourLogCreationController controller = loader.getController();
                controller.setTourLogForEditing(selectedLogVM);
                controller.setOnTourLogUpdatedCallback(updatedLog -> {
                    //nach dem Speichern neu laden
                    tourLogViewController.setTour(selectedTVM.getTour().getId());
                    //Optional: Details des gerade geänderten Logs anzeigen
                    tourLogViewController.showTourLogDetails(new TourLogViewModel(updatedLog));
                });

                Stage stage = new Stage();
                stage.setTitle("Edit Tour Log");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onDelete() {
        Tab selectedTab = detailTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return;
        }
        if (selectedTab.getText().equals("Tour Details")) {
            ObservableList<TourViewModel> selectedTVMs = tourListView.getSelectionModel().getSelectedItems();
            if (selectedTVMs.isEmpty()) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Lösche jede Tour über den Service und entferne sie aus der UI-Liste
                List<TourViewModel> toursToDelete = new ArrayList<>(selectedTVMs);
                toursToDelete.forEach(tvm -> {
                    tourService.deleteTourById(tvm.getTour().getId());
                    viewModel.getTourViewModels().remove(tvm);
                });
                tourListView.getSelectionModel().clearSelection();
                tourViewController.setTour(null);
                tourLogViewController.clear();
            }
        } else if (selectedTab.getText().equals("Tour Logs")) {
            TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
            if (selectedTVM == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a tour first.");
                alert.showAndWait();
                return;
            }
            ObservableList<TourLogViewModel> selectedLogVMs = tourLogViewController.getSelectedTourLogViewModels();
            ObservableList<TourLog> selectedLogs = FXCollections.observableArrayList();
            for (TourLogViewModel vm : selectedLogVMs) {
                selectedLogs.add(vm.getTourLog());
            }
            if (selectedLogs.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Tour Log Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select at least one tour log to delete.");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Tour Log(s)");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This action cannot be undone!");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedTVM.getTourLogViewModels().removeIf(vm -> selectedLogs.contains(vm.getTourLog()));
                selectedTVM.getTour().getTourLogs().removeAll(selectedLogs);

                for (TourLog log : selectedLogs) {
                    tourLogService.deleteTourLogById(log.getId());
                }

                // Aktualisiere die UI
                tourLogViewController.refreshLogs();
                tourLogViewController.clearDetails();
                tourLogViewController.clearSelection();
            }
        }
    }
    @FXML
    private void onGenerateTourReport() {
        TourViewModel selectedTVM = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTVM == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a tour to generate the report.");
            alert.showAndWait();
            return;
        }

        try {
            String filePath = "Reports/tour-report-" + selectedTVM.getTour().getId() + ".pdf";
            reportService.generateTourReportPdf(selectedTVM.getTour(), filePath);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText(null);
            alert.setContentText("Tour report generated successfully: " + filePath);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to generate the tour report.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onGenerateSummarizeReport(ActionEvent event) {
        try {
            String filePath = "Reports/summarize-report.pdf";
            List<Tour> tours = tourService.getAllTours(); // Alle Touren aus der Datenbank laden
            reportService.generateSummaryReportPdf(tours, filePath); // PDF-Report generieren
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText(null);
            alert.setContentText("Summarize report generated successfully: " + filePath);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to generate the summarize report.");
            alert.showAndWait();
        }
    }
    @FXML
    private void onExportJson() {
        try {
            List<Tour> tours = tourService.getAllTours();
            importExportService.exportToursToJson(tours);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Tours exported to 'import_export_json/tours_export.json'.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export tours.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onImportJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialDirectory(new File("import_export_json"));

        File selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        if (selectedFile != null) {
            try {
                List<Tour> importedTours = importExportService.importToursFromJson(selectedFile);
                for (Tour tour : importedTours) {
                    tourService.addTour(tour); // Fügt die Tour zur Datenbank hinzu
                }
                loadAllTours(); // Aktualisiert die UI
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Import Successful");
                alert.setHeaderText(null);
                alert.setContentText("Tours imported successfully.");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Import Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to import tours.");
                alert.showAndWait();
            }
        }
    }
}
