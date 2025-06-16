package org.example.tourplanner.businessLayer.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TourTest {

    @Test
    void testPopularityNoLogs() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 20);
        assertEquals("not rated yet", tour.getPopularity());
    }

    @Test
    void testPopularityWithLogs() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 20);
        tour.addTourLog(new TourLog("Log1", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 7));
        tour.addTourLog(new TourLog("Log2", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 9));
        assertEquals("8,0", tour.getPopularity());
    }

    @Test
    void testIsChildFriendlyNoLogsShortTime() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 100);
        assertTrue(tour.isChildFriendly());
    }

    @Test
    void testIsChildFriendlyNoLogsLongTime() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 200);
        assertFalse(tour.isChildFriendly());
    }

    @Test
    void testIsChildFriendlyWithLogsHighRating() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 100);
        tour.addTourLog(new TourLog("Log1", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 7));
        tour.addTourLog(new TourLog("Log2", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 8));
        assertTrue(tour.isChildFriendly());
    }

    @Test
    void testIsChildFriendlyWithLogsLowRating() {
        Tour tour = new Tour("Test", "desc", "start", "dest", "car", 10, 100);
        tour.addTourLog(new TourLog("Log1", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 4));
        tour.addTourLog(new TourLog("Log2", LocalDate.now(), LocalTime.now(), "c", "easy", 1, 1, 5));
        assertFalse(tour.isChildFriendly());
    }
}
