package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.models.Tour;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TourLogsController implements Initializable {

    private Tour currentTour;

    public TourLogsController(Tour currentTour) {
        this.currentTour = currentTour;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
