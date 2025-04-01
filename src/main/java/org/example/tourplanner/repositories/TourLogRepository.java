package org.example.tourplanner.repositories;

import org.example.tourplanner.data.models.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TourLogRepository extends JpaRepository<TourLog, UUID> {
    // Eigene Methoden bei Bedarf
}
