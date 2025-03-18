package org.example.tourplanner.mediators;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

// todo: mediator auch für tourlogs hinzufügen DONE
// todo: delete von mehreren tours möglich machen DONE
// todo: edit und delete für tourlogs DONE

//generische Klasse: <T> ist Platzhalter für Datentyp (Tour oder Tourlog), Datentyp wird beim Erzeugen einer Instanz gesetzt
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
        editButton.setDisable(selectedCount != 1); //Edit ist nur aktiv, wenn genau ein Item ausgewählt ist
        deleteButton.setDisable(selectedCount == 0); //Delete ist deaktiviert, wenn gar nichts ausgewählt ist.
    }

    //Aktiviert diesen Mediator, indem der Listener wieder hinzugefügt wird
    public void enable() {
        listView.getSelectionModel().getSelectedItems().addListener(listener);
        updateButtonState();
    }

    //Deaktiviert diesen Mediator, indem der Listener entfernt wird
    public void disable() {
        listView.getSelectionModel().getSelectedItems().removeListener(listener);
    }
}
