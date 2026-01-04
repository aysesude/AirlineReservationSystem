module com.airline {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.airline to javafx.fxml;
    opens com.airline.gui to javafx.fxml;
    opens com.airline.model to javafx.base;
    opens com.airline.model.enums to javafx.base;

    exports com.airline;
    exports com.airline.gui;
    exports com.airline.model;
    exports com.airline.model.enums;
    exports com.airline.manager;
    exports com.airline.service;
    exports com.airline.util;
}
