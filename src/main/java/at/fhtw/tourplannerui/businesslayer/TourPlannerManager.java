package at.fhtw.tourplannerui.businesslayer;

import at.fhtw.tourplannerui.models.Tour;
import java.util.List;
public interface TourPlannerManager {
    public List<Tour> getTours();
    public List<Tour> searchTours(String searchString, boolean caseSensitive);
}
