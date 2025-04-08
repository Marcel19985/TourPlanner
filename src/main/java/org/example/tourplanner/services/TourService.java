package org.example.tourplanner.services;

import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.data.repositories.TourRepository;
import org.example.tourplanner.data.repositories.TourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;

    @Autowired
    public TourService(TourRepository tourRepository, TourLogRepository tourLogRepository) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
    }

    // Tour-Operationen
    public List<Tour> getAllTours() {
        return tourRepository.findAllWithLogs();
    }

    public Tour getTourById(UUID tourId) {
        return tourRepository.findById(tourId).orElse(null);
    }

    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    public void deleteTourById(UUID tourId) {
        tourRepository.deleteById(tourId);
    }

    // TourLog-Operationen
    public List<TourLog> getTourLogsByTourId(UUID tourId) {
        return tourRepository.findTourLogsByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLogById(UUID tourLogId) {
        tourLogRepository.deleteById(tourLogId);
    }
}
