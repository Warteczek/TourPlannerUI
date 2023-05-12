package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManager;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManagerFactory;
import at.fhtw.tourplannerui.viewModel.tourPlanner.TourPlannerManager;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TourInformationController implements Initializable {

    public Label infoName;
    public Label infoFrom;
    public Label infoTo;
    public Label infoTransType;
    public Label infoDistance;
    public Label infoTime;
    public Label infoDescription;
    public Label popularity;
    public Label childFriendliness;

    public ImageView routeImage;
    public Label routeErrorLabel;
    private Tour currentTour;
    private TourInformationManager manager;

    public TourInformationController(Tour currentTour) {
        this.currentTour = currentTour;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manager= TourInformationManagerFactory.getManager();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        //load Route Image
        Runnable getRouteImage = () -> {
            Image image=manager.getRoute(currentTour);
            if(image==null){
                routeErrorLabel.setText("Could not load route");
            }else{
                routeErrorLabel.setText("");
                routeImage.setImage(image);
            }
        };
        executor.execute(getRouteImage);

        //load Tour Data
        String responseString=manager.getDistanceAndTime(currentTour);
        double distance_value = 0;
        double time = 0;
        if (responseString==""){
            currentTour.setDistance(null);
            currentTour.setTime(null);
            infoDistance.setText("Could not load distance");
            infoTime.setText("Could not load time");
        }
        else{
            try {
                JSONObject json_obj = new JSONObject(responseString);
                distance_value = json_obj.getDouble("distance");
                time = json_obj.getDouble("time");

                infoDistance.setText(String.valueOf(distance_value)+ " km");
                infoTime.setText(String.valueOf(Math.round((time/(60*60))*10)/10.0)+" hours");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        infoName.setText(currentTour.getId() + " " + currentTour.getName());
        infoFrom.setText(currentTour.getFrom());
        infoTo.setText(currentTour.getTo());
        infoTransType.setText(currentTour.getType());
        infoDescription.setText(currentTour.getDescription());

        executor.shutdown();
    }

    public void setPopularity(Integer popularityCount){
        popularity.setText(Integer.toString(popularityCount));
    }
    public void setChildFriendliness(String stringChildFriendliness){
        childFriendliness.setText(stringChildFriendliness);
    }
}
