package org.example.tourplanner.businessLayer.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.example.tourplanner.dataLayer.repositories.TourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TourLogService {

    private static final Logger logger = LogManager.getLogger(TourLogService.class);

    @Autowired
    private TourLogRepository tourLogRepository;

    public List<TourLog> getLogsByTourId(UUID tourId) {
        logger.info("Retrieving logs for tour with ID: {}", tourId);
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        logger.info("Saving a tour log: {}", tourLog.getName());
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLogById(UUID id) {
        logger.warn("Deleting tour log with ID: {}", id);
        tourLogRepository.deleteById(id);
    }

    /** Returns all logs for a tour, or only those filtered by name if term is not empty. */
    public List<TourLog> searchLogsByTour(UUID tourId, String term) {
        if (term == null || term.isBlank()) {
            logger.info("Retrieving all logs for tour with ID: {}", tourId);
            return tourLogRepository.findByTourId(tourId);
        }
        logger.info("Searching logs for tour with ID: {} and search term: {}", tourId, term.trim());
        return tourLogRepository.searchByTourIdAndName(tourId, term.trim());
    }
}