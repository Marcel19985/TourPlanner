open module org.example.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    requires org.json;
    requires javafx.web;
    requires java.persistence;
    requires spring.data.jpa;
    requires spring.beans;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.commons;

    exports org.example.tourplanner;
    exports org.example.tourplanner.data.database;
    exports org.example.tourplanner.data.models;
    exports org.example.tourplanner.ui.controllers;
    exports org.example.tourplanner.ui.viewmodels;
}
