module at.fhtw.tourplannerui {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhtw.tourplannerui to javafx.fxml;
    exports at.fhtw.tourplannerui;
}