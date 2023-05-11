package at.fhtw.tourplannerui.viewModel.tourInformation;

public class TourInformationManagerFactory {
    private static TourInformationManager manager;

    public static TourInformationManager getManager(){
        if(manager == null){
            manager= new TourInformationManagerImpl();
        }
        return manager;
    }
}
