package org.example.tourplanner.repositories;

import org.example.tourplanner.data.models.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TourRepository extends JpaRepository<Tour, UUID> {

    @Query("SELECT t FROM Tour t LEFT JOIN FETCH t.tourLogs")
    List<Tour> findAllWithLogs();

}