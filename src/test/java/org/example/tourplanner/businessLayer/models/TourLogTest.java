package org.example.tourplanner.businessLayer.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TourLogTest {

    @Test
    void
    constructor_shouldInitializeAllFields() {
        String name = "My Log";
        LocalDate date = LocalDate.of(2025, 1, 20);
        LocalTime time = LocalTime.of(14, 30);
        String comment = "Great tour!";
        String difficulty = "Medium";
        double totalDistance = 12.5;
        double totalTime = 75.0;
        int rating = 8;

        TourLog log = new TourLog(name, date, time, comment, difficulty, totalDistance, totalTime, rating);

        assertNotNull(log.getId(), "ID should be generated and not null");
        assertEquals(name, log.getName());
        assertEquals(date, log.getDate());
        assertEquals(time, log.getTime());
        assertEquals(comment, log.getComment());
        assertEquals(difficulty, log.getDifficulty());
        assertEquals(totalDistance, log.getTotalDistance(), 1e-6);
        assertEquals(totalTime, log.getTotalTime(), 1e-6);
        assertEquals(rating, log.getRating());
    }

    @Test
    void settersAndGetters_shouldRoundTrip() {
        TourLog log = new TourLog();

        String newName = "Evening Walk";
        LocalDate newDate = LocalDate.now();
        LocalTime newTime = LocalTime.now();
        String newComment = "Nice and easy.";
        String newDifficulty = "Easy";
        double newDistance = 5.0;
        double newTimeTotal = 45.0;
        int newRating = 5;
        Tour tour = new Tour();

        log.setName(newName);
        log.setDate(newDate);
        log.setTime(newTime);
        log.setComment(newComment);
        log.setDifficulty(newDifficulty);
        log.setTotalDistance(newDistance);
        log.setTotalTime(newTimeTotal);
        log.setRating(newRating);
        log.setTour(tour);

        assertEquals(newName, log.getName());
        assertEquals(newDate, log.getDate());
        assertEquals(newTime, log.getTime());
        assertEquals(newComment, log.getComment());
        assertEquals(newDifficulty, log.getDifficulty());
        assertEquals(newDistance, log.getTotalDistance(), 1e-6);
        assertEquals(newTimeTotal, log.getTotalTime(), 1e-6);
        assertEquals(newRating, log.getRating());
        assertSame(tour, log.getTour());
    }

    @Test
    void defaultConstructor_shouldHaveNullFields() {
        TourLog log = new TourLog();
        assertNull(log.getId());
        assertNull(log.getName());
        assertNull(log.getDate());
        assertNull(log.getTime());
        assertNull(log.getComment());
        assertNull(log.getDifficulty());
        assertEquals(0.0, log.getTotalDistance());
        assertEquals(0.0, log.getTotalTime());
        assertEquals(0, log.getRating());
        assertNull(log.getTour());
    }
}