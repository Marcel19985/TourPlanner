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
    requires kernel;
    requires layout;
    requires io;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.logging.log4j;

    exports org.example.tourplanner;
    exports org.example.tourplanner.presentationLayer.controllers;
    exports org.example.tourplanner.presentationLayer.viewmodels;
    exports org.example.tourplanner.businessLayer.models;
    exports org.example.tourplanner.businessLayer.services;
}