package org.example.tourplanner.businessLayer.services;

import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.dataLayer.repositories.TourLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourLogServiceTest {

    private TourLogRepository tourLogRepository;
    private TourLogService tourLogService;

    @BeforeEach
    void setUp() {
        tourLogRepository = mock(TourLogRepository.class);
        tourLogService = new TourLogService();
        // Setze das gemockte Repository per Reflection (da @Autowired nicht greift)
        try {
            var field = TourLogService.class.getDeclaredField("tourLogRepository");
            field.setAccessible(true);
            field.set(tourLogService, tourLogRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLogsByTourId() {
        UUID tourId = UUID.randomUUID();
        List<TourLog> logs = Arrays.asList(mock(TourLog.class), mock(TourLog.class));
        when(tourLogRepository.findByTourId(tourId)).thenReturn(logs);

        List<TourLog> result = tourLogService.getLogsByTourId(tourId);

        assertEquals(2, result.size());
        verify(tourLogRepository).findByTourId(tourId);
    }

    @Test
    void testSaveTourLog() {
        TourLog log = mock(TourLog.class);
        when(log.getName()).thenReturn("TestLog");
        when(tourLogRepository.save(log)).thenReturn(log);

        TourLog result = tourLogService.saveTourLog(log);

        assertEquals(log, result);
        verify(tourLogRepository).save(log);
    }

    @Test
    void testDeleteTourLogById() {
        UUID id = UUID.randomUUID();

        tourLogService.deleteTourLogById(id);

        verify(tourLogRepository).deleteById(id);
    }

    @Test
    void testSearchLogsByTour_withTerm() {
        UUID tourId = UUID.randomUUID();
        String term = "abc";
        List<TourLog> logs = Collections.singletonList(mock(TourLog.class));
        when(tourLogRepository.searchByTourIdAndName(tourId, term)).thenReturn(logs);

        List<TourLog> result = tourLogService.searchLogsByTour(tourId, term);

        assertEquals(1, result.size());
        verify(tourLogRepository).searchByTourIdAndName(tourId, term);
    }

    @Test
    void testSearchLogsByTour_withoutTerm() {
        UUID tourId = UUID.randomUUID();
        List<TourLog> logs = Arrays.asList(mock(TourLog.class), mock(TourLog.class));
        when(tourLogRepository.findByTourId(tourId)).thenReturn(logs);

        List<TourLog> result = tourLogService.searchLogsByTour(tourId, "");

        assertEquals(2, result.size());
        verify(tourLogRepository).findByTourId(tourId);
    }

    @Test
    void testSearchLogsByTour_withNullTerm() {
        UUID tourId = UUID.randomUUID();
        List<TourLog> logs = Arrays.asList(mock(TourLog.class), mock(TourLog.class));
        when(tourLogRepository.findByTourId(tourId)).thenReturn(logs);

        List<TourLog> result = tourLogService.searchLogsByTour(tourId, null);

        assertEquals(2, result.size());
        verify(tourLogRepository).findByTourId(tourId);
    }
}
