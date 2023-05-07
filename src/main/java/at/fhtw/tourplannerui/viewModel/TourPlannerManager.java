package at.fhtw.tourplannerui.viewModel;

import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.models.TourLog;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javafx.scene.image.Image;

import java.util.List;
public interface TourPlannerManager {
    public List<Tour> getTours();
    public List<Tour> searchTours(String searchString, boolean caseSensitive);
    public List<Tour> addTour(String tourName, String description, String startingLocation, String targetLocation, String transportType);
    public void deleteTours(List<String> ids);
    public Image getRoute(Tour currentTour);
    public String getDistanceAndTime(Tour currentTour);

    public List<TourLog> getTourLogs(String tourID);
    public void addTourLogForID(String id, String comment, Integer rating, Integer difficulty, Integer totalTime);
    public void deleteTourLog(Long id);
}
