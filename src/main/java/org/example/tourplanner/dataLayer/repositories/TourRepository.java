package org.example.tourplanner.dataLayer.repositories;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithLogs();

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.tourLogs WHERE t.id = :tourId")
    List<TourLog> findTourLogsByTourId(UUID tourId);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithTourLogs();
}
