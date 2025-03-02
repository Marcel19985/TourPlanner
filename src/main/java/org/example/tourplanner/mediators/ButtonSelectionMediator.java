package org.example.tourplanner.mediators;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.example.tourplanner.data.models.Tour;

public class ButtonSelectionMediator {
    private final Button editButton;
    private final Button deleteButton;
    private final ListView<Tour> tourListView;

    public ButtonSelectionMediator(Button editButton, Button deleteButton, ListView<Tour> tourListView) {
        this.editButton = editButton;
        this.deleteButton = deleteButton;
        this.tourListView = tourListView;

        //Listener auf Änderungen in der ListView-Selektion: aufgerufen sobale eine Liste ausgewählt oder de-ausgewählt wird
        this.tourListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tour>) change -> updateButtonState());

        //Initial setzen:
        updateButtonState();
    }

    private void updateButtonState() {
        int selectedCount = tourListView.getSelectionModel().getSelectedItems().size();

        //Bearbeiten nur möglich, wenn genau eine Tour ausgewählt ist:
        editButton.setDisable(selectedCount != 1);

        //Löschen ist deaktiviert, wenn nichts ausgewählt ist:
        deleteButton.setDisable(selectedCount == 0);
    }
}
