package org.example.tourplanner.mediators;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.example.tourplanner.data.models.Tour;

// todo: mediator auch für tourlogs hinzufügen
// todo: delete von mehreren tours möglich machen
// todo: edit und delete für tourlogs

public class ButtonSelectionMediator<T> {
    private final Button editButton;
    private final Button deleteButton;
    private final ListView<T> listView;
    private final ListChangeListener<T> listener;

    public ButtonSelectionMediator(Button editButton, Button deleteButton, ListView<T> listView) {
        this.editButton = editButton;
        this.deleteButton = deleteButton;
        this.listView = listView;
        this.listener = change -> updateButtonState();
        listView.getSelectionModel().getSelectedItems().addListener(listener);
        updateButtonState();
    }

    public void updateButtonState() {
        int selectedCount = listView.getSelectionModel().getSelectedItems().size();
        // Edit ist nur aktiv, wenn genau ein Item ausgewählt ist.
        editButton.setDisable(selectedCount != 1);
        // Delete ist deaktiviert, wenn gar nichts ausgewählt ist.
        deleteButton.setDisable(selectedCount == 0);
    }

    // Aktiviert diesen Mediator, indem der Listener wieder hinzugefügt wird.
    public void enable() {
        listView.getSelectionModel().getSelectedItems().addListener(listener);
        updateButtonState();
    }

    // Deaktiviert diesen Mediator, indem der Listener entfernt wird.
    public void disable() {
        listView.getSelectionModel().getSelectedItems().removeListener(listener);
    }
}
