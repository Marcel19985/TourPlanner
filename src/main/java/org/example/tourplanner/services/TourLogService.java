package org.example.tourplanner.services;

import org.example.tourplanner.data.models.TourLog;
import org.example.tourplanner.data.repositories.TourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TourLogService {

    @Autowired
    private TourLogRepository tourLogRepository;

    public List<TourLog> getLogsByTourId(UUID tourId) {
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog saveTourLog(TourLog tourLog) {
        return tourLogRepository.save(tourLog);
    }

    public void deleteTourLogById(UUID id) {
        tourLogRepository.deleteById(id);
    }
}