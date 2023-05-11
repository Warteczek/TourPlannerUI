package at.fhtw.tourplannerui.viewModel.tourLogs;

import at.fhtw.tourplannerui.models.TourLog;

import java.util.List;

public class TourLogManagerImpl implements TourLogManager{
    @Override
    public List<TourLog> getTourLogs(String tourID) {
        return null;
    }

    @Override
    public void addTourLogForID(String id, String comment, Integer rating, Integer difficulty, Integer totalTime) {

    }

    @Override
    public void deleteTourLog(Long id) {

    }

    @Override
    public void saveTourLog(TourLog currentTourLog) {

    }
}
