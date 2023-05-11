package at.fhtw.tourplannerui.viewModel.tourLogs;

import at.fhtw.tourplannerui.models.TourLog;

import java.util.List;

public interface TourLogManager {
    public List<TourLog> getTourLogs(String tourID);
    public void addTourLogForID(String id, String comment, Integer rating, Integer difficulty, Integer totalTime);
    public void deleteTourLog(Long id);
    public void saveTourLog(TourLog currentTourLog);
}
