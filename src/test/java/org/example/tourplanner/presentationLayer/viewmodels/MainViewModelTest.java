package org.example.tourplanner.presentationLayer.viewmodels;

class MainViewModelTest {
/*
    private MainViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new MainViewModel();
    }

    @Test
    void testAddTour() {
        // Act
        viewModel.addTour("Test Tour", "Description", "Vienna", "Graz", "Car");

        // Assert
        ObservableList<Tour> tours = viewModel.getTours();
        assertEquals(1, tours.size());
        assertEquals("Test Tour", tours.get(0).getName());
        assertEquals("Vienna", tours.get(0).getStart());
        assertEquals("Graz", tours.get(0).getDestination());
        assertEquals("Car", tours.get(0).getTransportType());
    }

    @Test
    void testAddTourLog() {
        // Arrange
        TourLog log = new TourLog("TourLog 1", LocalDate.parse("2025-03-10"), "Great trip", "Medium", 300, 180, 5);

        // Act
        viewModel.addTourLog(log);

        // Assert
        ObservableList<TourLog> logs = viewModel.getTourLogs();
        assertEquals(1, logs.size());
        assertEquals("Great trip", logs.get(0).getComment());
    }

    @Test
    void testSetTourLogsForTour() {
        // Arrange
        Tour tour = new Tour("Test Tour", "Description", "Vienna", "Graz", "Car");
        TourLog log1 = new TourLog("TourLog1", LocalDate.parse("2025-03-10"), "Great trip", "Medium", 300, 180, 5);
        TourLog log2 = new TourLog("TourLog2", LocalDate.parse("2025-05-19"), "Tiring", "Hard", 350, 240, 3);

        tour.addTourLog(log1);
        tour.addTourLog(log2);

        viewModel.addTour(tour.getName(), tour.getDescription(), tour.getStart(), tour.getDestination(), tour.getTransportType());

        // Act
        viewModel.setTourLogsForTour(tour);

        // Assert
        ObservableList<TourLog> logs = viewModel.getTourLogs();
        assertEquals(2, logs.size());
        assertTrue(logs.contains(log1));
        assertTrue(logs.contains(log2));
    }

    @Test
    void testGetToursInitiallyEmpty() {
        // Assert
        assertTrue(viewModel.getTours().isEmpty());
    }

    @Test
    void testGetTourLogsInitiallyEmpty() {
        // Assert
        assertTrue(viewModel.getTourLogs().isEmpty());
    }
    */
}
