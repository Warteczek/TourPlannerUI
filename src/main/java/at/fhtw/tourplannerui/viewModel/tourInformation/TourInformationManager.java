package at.fhtw.tourplannerui.viewModel.tourInformation;

import at.fhtw.tourplannerui.models.Tour;
import javafx.scene.image.Image;

public interface TourInformationManager {
    public Image getRoute(Tour currentTour);
    public String getDistanceAndTime(Tour currentTour);
    public void saveTour(Tour currentTour);
}
