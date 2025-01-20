module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires org.hibernate.orm.core;
    requires org.slf4j;
    requires jakarta.persistence;

    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;

    opens com.example.demo to javafx.fxml;
    opens controller to javafx.fxml; // Allow reflection for FXML
    opens model to org.hibernate.orm.core;

    exports controller;
    exports com.example.demo; // Export the package containing MainApp
    exports services;
    exports repositories;
    exports model;
    exports enums;
}
