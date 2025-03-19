package org.example.tourplanner.helpers;

//Exception wenn ein Ort beim erstellen einer Tour nich gefunden wird
public class LocationNotFoundException extends Exception {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
