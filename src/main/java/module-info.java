module at.fhtw.tourplannerui {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens at.fhtw.tourplannerui to javafx.fxml;
    exports at.fhtw.tourplannerui;
    exports at.fhtw.tourplannerui.viewModel;
    opens at.fhtw.tourplannerui.viewModel to javafx.fxml;
}