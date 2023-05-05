package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.models.TourLog;
import at.fhtw.tourplannerui.viewModel.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.TourPlannerManagerFactory;
import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfDocument;
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

import org.json.JSONException;
import org.json.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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
    public Label routeErrorLabel;

    public TextArea addTourLogComment;
    public TextField addTourLogDifficulty;
    public TextField addTourLogDuration;
    public TextField addTourLogRating;
    public TableView tourLogTable;




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
                if (responseString==""){
                    infoDistance.setText("Could not load distance");
                    infoTime.setText("Could not load time");
                }
                else{
                    try {
                        JSONObject json_obj = new JSONObject(responseString);

                        double distance_value = 0;
                        double time = 0;

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


                //load Tour Logs
                //List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());


                //TODO load logs


                executor.shutdown();
            }
        }));

        deleteListTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;
                ExecutorService executor = Executors.newSingleThreadExecutor();
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
                String responseString=manager.getDistanceAndTime(currentTour);
                if (responseString==""){
                    infoDistance.setText("Could not load distance");
                    infoTime.setText("Could not load time");
                }
                else{
                    try {
                        JSONObject json_obj = new JSONObject(responseString);
                        double distance_value = 0;
                        double time = 0;
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

    public void generateTourReport(ActionEvent actionEvent) {
        listTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;
            }
        }));

        deleteListTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)) {
                currentTour = (Tour) newValue;
            }
        }));

        if(currentTour == null)
            return;

        String home = System.getProperty("user.home");
        String dest = home + "/Downloads/" + currentTour.getName() + ".pdf";
        Document document = new Document();

        try{
            // Create a new PdfWriter object to write the document to a file
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open the document for writing
            document.open();

            document.add(new Paragraph("Name: " + currentTour.getName()));
            document.add(new Paragraph("Description: " + currentTour.getDescription()));
            document.add(new Paragraph("From: " + currentTour.getFrom()));
            document.add(new Paragraph("To: " + currentTour.getTo()));
            document.add(new Paragraph("Type: " + currentTour.getType()));
            //TODO distance and time are null
            document.add(new Paragraph("Distance: " + currentTour.getDistance()));
            document.add(new Paragraph("Time: " + currentTour.getTime()));

            //TODO tourlogs

            document.close();
        }catch(FileNotFoundException | DocumentException e){
            e.printStackTrace();
        }
    }

    public void generateSummarizeReport(ActionEvent actionEvent) {
        //TODO
    }

    public void addTourLog(){
        Tour currentTour= (Tour) listTours.getSelectionModel().getSelectedItem();

        String comment=addTourLogComment.textProperty().getValue();
        Integer rating=Integer.parseInt(addTourLogRating.textProperty().getValue());
        Integer difficulty=Integer.parseInt(addTourLogDifficulty.textProperty().getValue());
        Integer totalTime=Integer.parseInt(addTourLogDuration.textProperty().getValue());

        manager.addTourLogForID(currentTour.getId(), comment, rating, difficulty, totalTime);
    }
}