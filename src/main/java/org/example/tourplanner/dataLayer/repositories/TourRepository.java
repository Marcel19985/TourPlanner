package org.example.tourplanner.dataLayer.repositories;

import org.example.tourplanner.businessLayer.models.Tour;
import org.example.tourplanner.businessLayer.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    //Lädt alle Touren mit ihren TourLogs (left join = auch Touren ohne TourLogs
    @Query("SELECT DISTINCT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithLogs();

    //Lädt alle TourLogs einer bestimmten Tour
    @Query("""
        SELECT tl
        FROM Tour t
        JOIN t.tourLogs tl
        WHERE t.id = :tourId
""")
    List<TourLog> findTourLogsByTourId(@Param("tourId") UUID tourId);


    // Suche nach Tours, deren Name den Suchbegriff enthält (ignore case) und lade auch die Logs
    @Query("""
       SELECT DISTINCT t 
       FROM Tour t 
       LEFT JOIN FETCH t.tourLogs 
       WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Tour> searchByName(@Param("name") String name);

}
