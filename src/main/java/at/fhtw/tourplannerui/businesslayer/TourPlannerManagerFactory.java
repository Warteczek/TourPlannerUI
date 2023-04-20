package at.fhtw.tourplannerui.businesslayer;

import at.fhtw.tourplannerui.models.Tour;

import java.util.List;

public final class TourPlannerManagerFactory {
    private static TourPlannerManager manager;

    public static TourPlannerManager getManager(){
        if(manager == null){
            manager= new TourPlannerManagerImpl();
        }

        return manager;
    }
}
