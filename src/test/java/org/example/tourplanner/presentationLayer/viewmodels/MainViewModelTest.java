package org.example.tourplanner.presentationLayer.viewmodels;

import org.example.tourplanner.businessLayer.models.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainViewModelTest {

    private MainViewModel vm;
    private Tour sampleTour1;
    private Tour sampleTour2;

    @BeforeEach
    void setUp() {
        vm = new MainViewModel();
        //erstellt zwei Dummy-Touren:
        sampleTour1 = new Tour("A", "Desc1", "X", "Y", "car", 10, 20);
        sampleTour2 = new Tour("B", "Desc2", "P", "Q", "walk", 5, 15);
    }

    @Test
    void addTour_shouldAddOneViewModel() {
        assertTrue(vm.getTourViewModels().isEmpty());
        vm.addTour(sampleTour1);
        assertEquals(1, vm.getTourViewModels().size());
        assertEquals("A", vm.getTourViewModels().get(0).nameProperty().get());
    }

    @Test
    void deleteTours_shouldRemoveSpecifiedViewModels() {
        //fügt 2 tours zu TourViewModel hinzu:
        vm.addTour(sampleTour1);
        vm.addTour(sampleTour2);
        List<TourViewModel> all = vm.getTourViewModels();
        assertEquals(2, all.size());

        //lösche nur die erste:
        vm.deleteTours(List.of(all.get(0)));
        assertEquals(1, vm.getTourViewModels().size());
        assertEquals("B", vm.getTourViewModels().get(0).nameProperty().get()); //zweite Tour noch da
    }
}
