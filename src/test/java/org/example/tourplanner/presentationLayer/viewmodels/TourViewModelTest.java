package org.example.tourplanner.presentationLayer.viewmodels;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TourViewModelTest {

    private Tour tour;
    private TourLog log1;
    private TourLog log2;

    @BeforeEach
    void setUp() {
        //Erstellt Tour mit ein paar initialen Werten:
        tour = new Tour("Name", "Desc", "Start", "End", "Car", 10.0, 20.0);
        //Füge zwei TourLogs hinzu:
        log1 = new TourLog("Log1", LocalDate.now(), LocalTime.now(), "Comment1", "Easy", 5.0, 10.0, 8);
        log2 = new TourLog("Log2", LocalDate.now(), LocalTime.now(), "Comment2", "Hard", 7.0, 15.0, 6);
        tour.addTourLog(log1);
        tour.addTourLog(log2);
    }

    @Test
    void testConstructorInitializesPropertiesAndLogs() {
        TourViewModel vm = new TourViewModel(tour);

        assertEquals("Name", vm.nameProperty().get());
        assertEquals("Desc", vm.descriptionProperty().get());
        assertEquals("Start", vm.startProperty().get());
        assertEquals("End", vm.destinationProperty().get());
        assertEquals("Car", vm.transportTypeProperty().get());

        //Vergleich Tour Objekt mit Objekt vom ViewModel
        assertSame(tour, vm.getTour());
        //Die beiden Logs sollten in ViewModels umgewandelt worden sein
        assertEquals(2, vm.getTourLogViewModels().size());
    }

    @Test
    void testCopyConstructorIndependentProperties() {
        TourViewModel original = new TourViewModel(tour);
        //Verändere original:
        original.nameProperty().set("Modified");
        //Erzeuge Kopie:
        TourViewModel copy = new TourViewModel(original);

        //Kopie hat veränderten Wert:
        assertEquals("Modified", copy.nameProperty().get());
        copy.nameProperty().set("CopyName"); //Ändere die Kopie — original bleibt unberührt
        assertEquals("CopyName", copy.nameProperty().get()); //Kopie hat veränderten Wert
        assertEquals("Modified", original.nameProperty().get()); //Original hat immer noch den ursprünglichen Wert
    }

    @Test
    void testCopyFromEditingCloneBackToOriginal() {
        TourViewModel original = new TourViewModel(tour);
        TourViewModel editing = new TourViewModel(original);

        //Werte im editing-Clone ändern:
        editing.nameProperty().set("NewName");
        editing.descriptionProperty().set("NewDesc");
        editing.startProperty().set("NewStart");
        editing.destinationProperty().set("NewDest");
        editing.transportTypeProperty().set("Bike");

        //Auf original anwenden:
        original.copyFrom(editing);

        //Properties im original sollten übernommen sein:
        assertEquals("NewName", original.nameProperty().get());
        assertEquals("NewDesc", original.descriptionProperty().get());
        assertEquals("NewStart", original.startProperty().get());
        assertEquals("NewDest", original.destinationProperty().get());
        assertEquals("Bike", original.transportTypeProperty().get());

        //Und auch das im viewModel befindliche Tour Objekt:
        assertEquals("NewName", tour.getName());
        assertEquals("NewDesc", tour.getDescription());
    }

    @Test
    void testUpdateTourPersistsToTourObject() {
        TourViewModel vm = new TourViewModel(tour);

        //Properties ändern
        vm.nameProperty().set("ChangedName");
        vm.descriptionProperty().set("ChangedDesc");
        vm.startProperty().set("S2");
        vm.destinationProperty().set("D2");
        vm.transportTypeProperty().set("Walk");

        //Tour Objekt sollte noch unberührt sein:
        assertEquals("Name", tour.getName());

        //Mit Update ändert sich auch Tour Objekt:
        vm.updateTour();

        //Check, ob Tour Objekt richtig upgedated wurde:
        assertEquals("ChangedName", tour.getName());
        assertEquals("ChangedDesc", tour.getDescription());
        assertEquals("S2", tour.getStart());
        assertEquals("D2", tour.getDestination());
        assertEquals("Walk", tour.getTransportType());
    }

    @Test
    void testAddTourLogAddsToViewModelAndTourObject() {
        TourViewModel vm = new TourViewModel(tour);
        int initialCount = vm.getTourLogViewModels().size();

        //Neuen TourLog erzeugen:
        TourLog newLog = new TourLog("Log3", LocalDate.now(), LocalTime.now(), "Comment3", "Medium", 3.0, 5.0, 7);
        vm.addTourLog(newLog);

        //Tour Objekt enthält das neue Log:
        assertTrue(tour.getTourLogs().contains(newLog));
        //Und die ViewModel-Liste ist um ein Element gewachsen:
        assertEquals(initialCount + 1, vm.getTourLogViewModels().size());
    }
}