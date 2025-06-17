package org.example.tourplanner.businessLayer.services;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.dataLayer.repositories.TourLogRepository;
import org.example.tourplanner.dataLayer.repositories.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourServiceTest {

    private TourRepository tourRepository;
    private TourLogRepository tourLogRepository;
    private TourService tourService;

    @BeforeEach
    void setUp() {
        tourRepository = mock(TourRepository.class);
        tourLogRepository = mock(TourLogRepository.class);
        tourService = new TourService(tourRepository, tourLogRepository);
    }

    @Test
    void testGetAllTours() {
        List<Tour> tours = Arrays.asList(mock(Tour.class), mock(Tour.class));
        when(tourRepository.findAllWithLogs()).thenReturn(tours);

        List<Tour> result = tourService.getAllTours();

        assertEquals(2, result.size());
        verify(tourRepository).findAllWithLogs();
    }

    @Test
    void testGetTourById_found() {
        UUID id = UUID.randomUUID();
        Tour tour = mock(Tour.class);
        when(tourRepository.findById(id)).thenReturn(Optional.of(tour));

        Tour result = tourService.getTourById(id);

        assertEquals(tour, result);
        verify(tourRepository).findById(id);
    }

    @Test
    void testGetTourById_notFound() {
        UUID id = UUID.randomUUID();
        when(tourRepository.findById(id)).thenReturn(Optional.empty());

        Tour result = tourService.getTourById(id);

        assertNull(result);
        verify(tourRepository).findById(id);
    }

    @Test
    void testAddTour() {
        Tour tour = mock(Tour.class);
        tourService.addTour(tour);
        verify(tourRepository).save(tour);
    }

    @Test
    void testSaveTour() {
        Tour tour = mock(Tour.class);
        when(tourRepository.save(tour)).thenReturn(tour);

        Tour result = tourService.saveTour(tour);

        assertEquals(tour, result);
        verify(tourRepository).save(tour);
    }

    @Test
    void testDeleteTourById() {
        UUID id = UUID.randomUUID();
        tourService.deleteTourById(id);
        verify(tourRepository).deleteById(id);
    }

    @Test
    void testGetTourLogsByTourId() {
        UUID id = UUID.randomUUID();
        List<TourLog> logs = Arrays.asList(mock(TourLog.class), mock(TourLog.class));
        when(tourRepository.findTourLogsByTourId(id)).thenReturn(logs);

        List<TourLog> result = tourService.getTourLogsByTourId(id);

        assertEquals(2, result.size());
        verify(tourRepository).findTourLogsByTourId(id);
    }

    @Test
    void testSaveTourLog() {
        TourLog log = mock(TourLog.class);
        when(tourLogRepository.save(log)).thenReturn(log);

        TourLog result = tourService.saveTourLog(log);

        assertEquals(log, result);
        verify(tourLogRepository).save(log);
    }

    @Test
    void testDeleteTourLogById() {
        UUID id = UUID.randomUUID();
        tourService.deleteTourLogById(id);
        verify(tourLogRepository).deleteById(id);
    }

    @Test
    void testSearchToursByName() {
        String name = "Test";
        List<Tour> tours = Arrays.asList(mock(Tour.class), mock(Tour.class));
        when(tourRepository.searchByName(name)).thenReturn(tours);

        List<Tour> result = tourService.searchToursByName(name);

        assertEquals(2, result.size());
        verify(tourRepository).searchByName(name);
    }

    @Test
    void testSearchTours_withNameAndFilters() {
        String name = "Test";
        Tour t1 = mock(Tour.class);
        Tour t2 = mock(Tour.class);
        when(t1.getAverageRating()).thenReturn(7.0);
        when(t2.getAverageRating()).thenReturn(5.0);
        when(t1.isChildFriendly()).thenReturn(true);
        when(t2.isChildFriendly()).thenReturn(false);

        List<Tour> tours = Arrays.asList(t1, t2);
        when(tourRepository.searchByName(name)).thenReturn(tours);

        List<Tour> result = tourService.searchTours(name, 6.0, true);

        assertEquals(1, result.size());
        assertTrue(result.contains(t1));
        assertFalse(result.contains(t2));
    }

    @Test
    void testSearchTours_withoutName() {
        Tour t1 = mock(Tour.class);
        when(t1.getAverageRating()).thenReturn(8.0);
        when(t1.isChildFriendly()).thenReturn(true);

        List<Tour> tours = Collections.singletonList(t1);
        when(tourRepository.findAllWithLogs()).thenReturn(tours);

        List<Tour> result = tourService.searchTours(null, 7.0, false);

        assertEquals(1, result.size());
        assertTrue(result.contains(t1));
    }
}
