package at.fhtw.tourplannerui.viewModel.tourPlanner;

public final class TourPlannerManagerFactory {
    private static TourPlannerManager manager;

    public static TourPlannerManager getManager(){
        if(manager == null){
            manager= new TourPlannerManagerImpl();
        }

        return manager;
    }
}
