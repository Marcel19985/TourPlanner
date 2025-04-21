package org.example.tourplanner.dataLayer.repositories;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TourLogRepository extends JpaRepository<TourLog, UUID> {
    //ruft alle TourLogs ab, die zu einer bestimmten Tour gehören:
    @Query("SELECT tl FROM TourLog tl WHERE tl.tour.id = :tourId")
    List<TourLog> findByTourId(@Param("tourId") UUID tourId);

    //Für Tour Log Suche:
    @Query("""
      SELECT tl
      FROM TourLog tl
      WHERE tl.tour.id = :tourId
        AND LOWER(tl.name) LIKE LOWER(CONCAT('%', :term, '%'))
    """)
    List<TourLog> searchByTourIdAndName(
            @Param("tourId") UUID tourId,
            @Param("term") String term);
}

