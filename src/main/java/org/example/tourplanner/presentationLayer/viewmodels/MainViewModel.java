package org.example.tourplanner.presentationLayer.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tourplanner.businessLayer.models.Tour;

public class MainViewModel {
    // Liste von TourViewModel statt Tour-Objekten
    private final ObservableList<TourViewModel> tourViewModels = FXCollections.observableArrayList();

    public ObservableList<TourViewModel> getTourViewModels() {
        return tourViewModels;
    }

    // Fügt eine Tour über ihr ViewModel hinzu
    public void addTour(Tour newTour) {
        tourViewModels.add(new TourViewModel(newTour));
    }

    // Löscht Touren (hier als ViewModel) – zentral über das ViewModel
    public void deleteTours(java.util.List<TourViewModel> toursToDelete) {
        tourViewModels.removeAll(toursToDelete);
    }

    // Aktualisiert eine Tour, indem die entsprechenden ViewModel-Properties angepasst werden
    public void updateTour(TourViewModel updatedTour) {
        for (TourViewModel tvm : tourViewModels) {
            if (tvm.getTour().getId().equals(updatedTour.getTour().getId())) {
                tvm.updateTour();
                break;
            }
        }
    }
}
