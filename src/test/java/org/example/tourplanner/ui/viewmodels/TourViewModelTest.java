package org.example.tourplanner.ui.viewmodels;

import org.example.tourplanner.data.models.Tour;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TourViewModelTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testUpdateTour() {
        //Tour-Objekt:
        Tour tour = new Tour("Old Tour", "Old Description", "Old Start", "Old Destination", "Car", 100, 60);

        TourViewModel viewModel = new TourViewModel(tour); //erstellt ViewModel mit Tour-Objekt

        //Ändere ViewModel-Properties:
        viewModel.nameProperty().set("New Tour");
        viewModel.descriptionProperty().set("New Description");
        viewModel.startProperty().set("New Start");
        viewModel.destinationProperty().set("New Destination");
        viewModel.transportTypeProperty().set("Bike");

        viewModel.updateTour();

        //Überprüfe, ob Änderungen übernommen wurden:
        assertEquals("New Tour", tour.getName());
        assertEquals("New Description", tour.getDescription());
        assertEquals("New Start", tour.getStart());
        assertEquals("New Destination", tour.getDestination());
        assertEquals("Bike", tour.getTransportType());
    }
}