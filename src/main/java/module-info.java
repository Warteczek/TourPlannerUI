module at.fhtw.tourplannerui {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires com.fasterxml.jackson.databind;


    opens at.fhtw.tourplannerui to javafx.fxml;
    exports at.fhtw.tourplannerui;
    exports at.fhtw.tourplannerui.view;
    opens at.fhtw.tourplannerui.view to javafx.fxml;

    exports at.fhtw.tourplannerui.models to com.fasterxml.jackson.databind;
}