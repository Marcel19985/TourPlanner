package org.example.tourplanner.businessLayer.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.dataLayer.repositories.TourRepository;
import org.example.tourplanner.dataLayer.repositories.TourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TourService {

    private static final Logger logger = LogManager.getLogger(TourService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;

    @Autowired
    public TourService(TourRepository tourRepository, TourLogRepository tourLogRepository) {
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
        logger.info("TourService initialized.");
    }

    // Tour operations
    public List<Tour> getAllTours() {
        logger.info("Retrieving all tours with logs.");
        return tourRepository.findAllWithLogs();
    }

    public Tour getTourById(UUID tourId) {
        logger.info("Retrieving tour with ID: {}", tourId);
        return tourRepository.findById(tourId).orElse(null);
    }

    public void addTour(Tour tour) {
        logger.info("Adding a new tour: {}", tour.getName());
        tourRepository.save(tour);
    }

    public Tour saveTour(Tour tour) {
        logger.info("Saving tour: {}", tour.getName());
        return tourRepository.save(tour);
    }

    public void deleteTourById(UUID tourId) {
        logger.warn("Deleting tour with ID: {}", tourId);
        tourRepository.deleteById(tourId);
    }

    // TourLog operations
    public List<TourLog> getTourLogsByTourId(UUID tourId) {
        logger.info("Retrieving logs for tour with ID: {}", tourId);
        return tourRepository.findTourLogsByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        logger.info("Saving a tour log: {}", tourLog.getName());
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLogById(UUID tourLogId) {
        logger.warn("Deleting tour log with ID: {}", tourLogId);
        tourLogRepository.deleteById(tourLogId);
    }

    //Suche nach Touren:
    public List<Tour> searchToursByName(String name) {
        logger.info("Searching for tours with name: {}", name);
        return tourRepository.searchByName(name);
    }

    public List<Tour> searchTours(String name,
                                  double minPopularity,
                                  boolean onlyChildFriendly) {
        logger.info("Searching for tours with filters - Name: {}, Min popularity: {}, Only child friendly: {}", name, minPopularity, onlyChildFriendly);
        List<Tour> base = (name == null || name.isBlank())
                ? tourRepository.findAllWithLogs()
                : tourRepository.searchByName(name);
        return base.stream()
                .filter(t -> t.getAverageRating() >= minPopularity)
                .filter(t -> !onlyChildFriendly || t.isChildFriendly())
                .toList();
    }
}