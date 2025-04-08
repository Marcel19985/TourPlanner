package org.example.tourplanner.services;

import org.example.tourplanner.data.models.Tour;
import org.example.tourplanner.data.repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    public List<Tour> getAllTours() {
        return tourRepository.findAllWithTourLogs(); // Verwende die neue Methode
    }

    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    public void deleteTourById(UUID id) {
        tourRepository.deleteById(id);
    }
}
