package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.viewModel.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.TourPlannerManagerFactory;
import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Initializable {

    public ListView listTours;
    public ListView deleteListTours;
    public TextField searchField;
    public TextField addNameTour;
    public TextArea addDescriptionTour;
    public TextField addStartLocation;
    public TextField addTransportType;
    public TextField addDestinationTour;
    public TabPane tourTabPane;
    public Tab tourListTab;
    public Tab addTourTab;
    public Tab deleteTourTab;
    public VBox generalInformationContainer;
    public Label infoName;
    public Label infoFrom;
    public Label infoTo;
    public Label infoTransType;
    public Label infoDistance;
    public Label infoTime;
    public Label infoDescription;
    public ImageView routeImage;



    private ObservableList<Tour> tourList;
    private ObservableList<Tour> tourListDelete;
    private Tour currentTour;
    private TourPlannerManager manager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        manager= TourPlannerManagerFactory.getManager();

        setUpListView();

        formatCells();

        setCurrentTour();
        deleteListTours.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void setUpListView(){
        tourList= FXCollections.observableArrayList();
        tourList.addAll(manager.getTours());

        listTours.setItems(tourList);
        deleteListTours.setItems(tourList);
    }

    private void setCurrentTour(){
        listTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;
                // create an ExecutorService with a single thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                // create a new Runnable to set the distance text and run it in a separate thread
                Runnable getRouteImage = () -> {
                    try {
                        URL url = new URL("http://localhost:8087/map?start="+currentTour.getFrom()+"&end="+currentTour.getTo());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        // read the response from the server as a byte array
                        byte[] response = new byte[conn.getContentLength()];
                        InputStream in = conn.getInputStream();
                        int bytesRead = 0;
                        while (bytesRead < response.length) {
                            int count = in.read(response, bytesRead, (response.length - bytesRead));
                            if (count == -1) {
                                break;
                            }
                            bytesRead += count;
                        }
                        in.close();

                        // create a new ByteArrayInputStream from the response byte array
                        ByteArrayInputStream bais = new ByteArrayInputStream(response);

                        Image image = new Image(bais);

                        routeImage.setImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                };
                executor.execute(getRouteImage);


                infoName.setText(currentTour.getId() + " " + currentTour.getName());
                infoFrom.setText(currentTour.getFrom());
                infoTo.setText(currentTour.getTo());
                infoTransType.setText(currentTour.getType());

                System.out.println(currentTour.getDistance());

                //infoDistance.setText(currentTour.getDistance().toString());
                infoDistance.setText("");
                //infoTime.setText(currentTour.getTime().toString());
                infoDescription.setText(currentTour.getDescription());

                executor.shutdown();
            }
        }));

        deleteListTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;
                // create an ExecutorService with a single thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                // create a new Runnable to set the distance text and run it in a separate thread
                Runnable getRouteImage = () -> {
                    try {
                        URL url = new URL("http://localhost:8087/map?start="+currentTour.getFrom()+"&end="+currentTour.getTo());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        // read the response from the server as a byte array
                        byte[] response = new byte[conn.getContentLength()];
                        InputStream in = conn.getInputStream();
                        int bytesRead = 0;
                        while (bytesRead < response.length) {
                            int count = in.read(response, bytesRead, (response.length - bytesRead));
                            if (count == -1) {
                                break;
                            }
                            bytesRead += count;
                        }
                        in.close();

                        // create a new ByteArrayInputStream from the response byte array
                        ByteArrayInputStream bais = new ByteArrayInputStream(response);

                        Image image = new Image(bais);

                        routeImage.setImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                };
                executor.execute(getRouteImage);


                infoName.setText(currentTour.getId() + " " + currentTour.getName());
                infoFrom.setText(currentTour.getFrom());
                infoTo.setText(currentTour.getTo());
                infoTransType.setText(currentTour.getType());

                System.out.println(currentTour.getDistance());

                //infoDistance.setText(currentTour.getDistance().toString());
                infoDistance.setText("");
                //infoTime.setText(currentTour.getTime().toString());
                infoDescription.setText(currentTour.getDescription());

                executor.shutdown();
            }
        }));

    }

    private void formatCells(){
        listTours.setCellFactory(param -> new ListCell<Tour>(){
            protected void updateItem(Tour item, boolean empty){
                super.updateItem(item, empty);

                if(empty || (item == null) || (item.getName() == null)){
                    setText(null);
                } else{
                    setText(item.getName());
                }
            }
        });
        deleteListTours.setCellFactory(param -> new ListCell<Tour>(){
            protected void updateItem(Tour item, boolean empty){
                super.updateItem(item, empty);

                if(empty || (item == null) || (item.getName() == null)){
                    setText(null);
                } else{
                    setText(item.getName());
                }
            }
        });
    }

    public void quitApplication(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void searchAction(ActionEvent actionEvent) {

        tourList.clear();
        //tourListDelete.clear();

        List<Tour> tours= manager.searchTours(searchField.textProperty().getValue(), false);

        tourList.addAll(tours);
        //tourListDelete.addAll(tours);
    }

    public void clearAction(ActionEvent actionEvent) {
        tourList.clear();
        //tourListDelete.clear();
        searchField.textProperty().setValue("");

        List<Tour> tours = manager.getTours();
        tourList.addAll(tours);
        //tourListDelete.addAll(tours);
    }

    //TODO does not work anymore
    public void addAction(ActionEvent actionEvent) {

        tourList.clear();
        //TODO causes Exceptions
        //tourListDelete.clear();

        List<Tour> tours= manager.addTour(addNameTour.textProperty().getValue(),
                addDescriptionTour.textProperty().getValue(),
                addStartLocation.textProperty().getValue(),
                addDestinationTour.textProperty().getValue(),
                addTransportType.textProperty().getValue());

        tourList.addAll(tours);
        //TODO causes Exceptions
        //tourListDelete.addAll(tours);
        tourTabPane.getSelectionModel().select(tourListTab);
        addNameTour.textProperty().setValue("");
        addDescriptionTour.textProperty().setValue("");
        addStartLocation.textProperty().setValue("");
        addTransportType.textProperty().setValue("");
        addDestinationTour.textProperty().setValue("");
    }

    public void deleteAction(ActionEvent actionEvent) {
        ObservableList<Tour> selectedItems = deleteListTours.getSelectionModel().getSelectedItems();

        List<String> deleteIDs=new ArrayList<String>();
        for(Tour tour:selectedItems){
            deleteIDs.add(tour.getId());
        }
        manager.deleteTours(deleteIDs);
        setUpListView();
    }
}