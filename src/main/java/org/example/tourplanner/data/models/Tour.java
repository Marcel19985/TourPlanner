package org.example.tourplanner.data.models;

public class Tour {
    private String name;
    private String description;
    private double distance;
    private double estimatedTime;
    // Weitere Felder wie Transportmittel, Route (Map), etc.

    public Tour(String name, String description) {
        this.name = name;
        this.description = description;
    }
    @Override
    public String toString() {
        return name + " " + description;
    }


    // Getter und Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
