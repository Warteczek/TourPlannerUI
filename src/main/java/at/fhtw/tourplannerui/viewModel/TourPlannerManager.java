package at.fhtw.tourplannerui.viewModel;

import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;
public interface TourPlannerManager {
    public List<Tour> getTours();
    public List<Tour> searchTours(String searchString, boolean caseSensitive);
    public List<Tour> addTour(String tourName, String description, String startingLocation, String targetLocation, String transportType);
}
