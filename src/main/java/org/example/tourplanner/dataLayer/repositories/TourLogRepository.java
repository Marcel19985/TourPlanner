package org.example.tourplanner.dataLayer.repositories;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TourLogRepository extends JpaRepository<TourLog, UUID> {
    // Eigene Methoden bei Bedarf
    @Query("SELECT tl FROM TourLog tl WHERE tl.tour.id = :tourId")
    List<TourLog> findByTourId(@Param("tourId") UUID tourId);

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithTourLogs();
}

