package at.fhtw.tourplannerui.viewModel.tourLogs;


public final class TourLogManagerFactory {
    private static TourLogManager manager;

    public static TourLogManager getManager(){
        if(manager == null){
            manager= new TourLogManagerImpl();
        }
        return manager;
    }
}
