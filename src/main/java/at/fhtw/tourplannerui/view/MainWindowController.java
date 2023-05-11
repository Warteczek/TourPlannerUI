package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.TourLog;
import at.fhtw.tourplannerui.viewModel.tourPlanner.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.tourPlanner.TourPlannerManagerFactory;
import at.fhtw.tourplannerui.models.Tour;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    public Label popularity;
    public Label childFriendliness;

    public ImageView routeImage;
    public Label routeErrorLabel;

    public TextArea addTourLogComment;
    public TextField addTourLogDifficulty;
    public TextField addTourLogDuration;
    public TextField addTourLogRating;
    public TableView tourLogTable;
    public TableColumn tourLogTableID;
    public TableColumn tourLogTableCreationTime;
    public TableColumn tourLogTableComment;
    public TableColumn tourLogTableDifficulty;
    public TableColumn tourLogTableTotalTime;
    public TableColumn tourLogTableRating;
    public MenuBar myMenuBar;


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

        //initialize TableView
        tourLogTableID.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("id"));
        tourLogTableCreationTime.setCellValueFactory(new PropertyValueFactory<TourLog, Timestamp>("creationTime"));
        tourLogTableComment.setCellValueFactory(new PropertyValueFactory<TourLog, String>("comment"));
        tourLogTableDifficulty.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("difficulty"));
        tourLogTableTotalTime.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("totalTime"));
        tourLogTableRating.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("rating"));
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
                double distance_value = 0;
                double time = 0;
                if (responseString==""){
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


                //load Tour Logs
                List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
                ObservableList<TourLog> databaseTourLogs=FXCollections.observableArrayList(tourLogs);
                for(TourLog log : databaseTourLogs){
                    log.setTotalTime(log.getTotalTime()/60);
                }
                tourLogTable.setItems(databaseTourLogs);


                Integer popularityCount=0;
                double difficultySum=0.0;

                for (TourLog tourLog:tourLogs) {
                    difficultySum=difficultySum+tourLog.getDifficulty();
                    popularityCount=popularityCount+1;
                }


                if(popularityCount==0 || infoDistance.getText().equals("Could not load distance") || infoTime.getText().equals("Could not load time")){
                    childFriendliness.setText("Not sure");
                }else{
                    double averageDifficulty=difficultySum/popularityCount;
                    if(averageDifficulty>5.0 || distance_value>500.0){
                        childFriendliness.setText("No");
                    }else{
                        childFriendliness.setText("Yes");
                    }
                }
                popularity.setText(Integer.toString(popularityCount));

                executor.shutdown();
            }
        }));

        deleteListTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
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
                double distance_value = 0;
                double time = 0;
                if (responseString==""){
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


                //load Tour Logs
                List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
                ObservableList<TourLog> databaseTourLogs=FXCollections.observableArrayList(tourLogs);
                for(TourLog log : databaseTourLogs){
                    log.setTotalTime(log.getTotalTime()/60);
                }
                tourLogTable.setItems(databaseTourLogs);


                Integer popularityCount=0;
                double difficultySum=0.0;

                for (TourLog tourLog:tourLogs) {
                    difficultySum=difficultySum+tourLog.getDifficulty();
                    popularityCount=popularityCount+1;
                }


                if(popularityCount==0 || infoDistance.getText().equals("Could not load distance") || infoTime.getText().equals("Could not load time")){
                    childFriendliness.setText("Not sure");
                }else{
                    double averageDifficulty=difficultySum/popularityCount;
                    if(averageDifficulty>5.0 || distance_value>500.0){
                        childFriendliness.setText("No");
                    }else{
                        childFriendliness.setText("Yes");
                    }
                }
                popularity.setText(Integer.toString(popularityCount));

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
    }

    public void clearAction(ActionEvent actionEvent) {
        tourList.clear();
        //tourListDelete.clear();
        searchField.textProperty().setValue("");

        List<Tour> tours = manager.getTours();
        tourList.addAll(tours);
        //tourListDelete.addAll(tours);
    }

    public void addAction(ActionEvent actionEvent) {

        tourList.clear();

        List<Tour> tours= manager.addTour(addNameTour.textProperty().getValue(),
                addDescriptionTour.textProperty().getValue(),
                addStartLocation.textProperty().getValue(),
                addDestinationTour.textProperty().getValue(),
                addTransportType.textProperty().getValue());

        tourList.addAll(tours);

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

            document.add(new Paragraph("Tour: "));
            document.add(new Paragraph("Name: " + currentTour.getName()));
            document.add(new Paragraph("Description: " + currentTour.getDescription()));
            document.add(new Paragraph("From: " + currentTour.getFrom()));
            document.add(new Paragraph("To: " + currentTour.getTo()));
            document.add(new Paragraph("Type: " + currentTour.getType()));
            //TODO distance and time are null
            document.add(new Paragraph("Distance: " + currentTour.getDistance()));
            document.add(new Paragraph("Time: " + currentTour.getTime()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Tourlogs: "));

            List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
            for(TourLog log : tourLogs){
                document.add(new Paragraph("Creation Time: " + log.getCreationTime()));
                document.add(new Paragraph("Comment: " + log.getComment()));
                document.add(new Paragraph("Total Time: " + log.getTotalTime()));
                document.add(new Paragraph("Rating: " + log.getRating()));
                document.add(new Paragraph("Difficulty: " + log.getDifficulty()));
                document.add(new Paragraph(" "));
            }

            document.close();
        }catch(FileNotFoundException | DocumentException e){
            e.printStackTrace();
        }
    }

    public double getAvg(List<Integer> list){
        OptionalDouble avgTime = list
                .stream()
                .mapToDouble(i -> i)
                .average();

        return avgTime.isPresent() ? avgTime.getAsDouble() : 0;
    }

    public void generateSummarizeReport(ActionEvent actionEvent) {
        String home = System.getProperty("user.home");
        String dest = home + "/Downloads/summarize-report.pdf";
        Document document = new Document();

        try{
            // Create a new PdfWriter object to write the document to a file
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open the document for writing
            document.open();
        }catch(FileNotFoundException | DocumentException e){
            e.printStackTrace();
        }

        tourList.forEach(tour -> {
            List<TourLog> tourLogs= manager.getTourLogs(tour.getId());

            List<Integer> timeList = new ArrayList<>();
            List<Integer> diffList = new ArrayList<>();
            List<Integer> ratingList = new ArrayList<>();

            for(TourLog log : tourLogs){
                if(log.getTotalTime() != null)
                    timeList.add(log.getTotalTime());
                if(log.getDifficulty() != null)
                    diffList.add(log.getDifficulty());
                if(log.getRating() != null)
                    ratingList.add(log.getRating());
            }

            try {
                document.add(new Paragraph(tour.getName() + ": "));
                document.add(new Paragraph("Average Time: " + String.valueOf(getAvg(timeList))));
                document.add(new Paragraph("Average Difficulty: " + String.valueOf(getAvg(diffList))));
                document.add(new Paragraph("Average Rating: " + String.valueOf(getAvg(ratingList))));
                document.add(new Paragraph(" "));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        });

        document.close();
    }

    public void addTourLog(){
        Tour currentTour= (Tour) listTours.getSelectionModel().getSelectedItem();

        String comment=addTourLogComment.textProperty().getValue();
        Integer rating=0;
        Integer difficulty=0;
        Integer totalTime=0;

        try {
            rating=Integer.parseInt(addTourLogRating.textProperty().getValue());
        } catch (NumberFormatException e) {
            addTourLogRating.setText("Please put in a number");
            return;
        }

        try {
            difficulty=Integer.parseInt(addTourLogDifficulty.textProperty().getValue());
        } catch (NumberFormatException e) {
            addTourLogDifficulty.setText("Please put in a number");
            return;
        }

        try {
            totalTime=Integer.parseInt(addTourLogDuration.textProperty().getValue());
        } catch (NumberFormatException e) {
            addTourLogDuration.setText("Please put in a number");
            return;
        }

        if(rating>10 || rating<0){
            addTourLogRating.setText("Not a valid number");
            return;
        }
        if(difficulty>10 || difficulty<0){
            addTourLogDifficulty.setText("Not a valid number");
            return;
        }

        totalTime=totalTime*60;

        manager.addTourLogForID(currentTour.getId(), comment, rating, difficulty, totalTime);

        //load Tour Logs
        List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
        ObservableList<TourLog> databaseTourLogs=FXCollections.observableArrayList(tourLogs);
        for(TourLog log : databaseTourLogs){
            log.setTotalTime(log.getTotalTime()/60);
        }
        tourLogTable.setItems(databaseTourLogs);
    }

    public void deleteTourLogAction(ActionEvent actionEvent) {
        TourLog selectedTourLog = (TourLog) tourLogTable.getSelectionModel().getSelectedItem();
        manager.deleteTourLog(selectedTourLog.getId());

        //load Tour Logs
        List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
        ObservableList<TourLog> databaseTourLogs=FXCollections.observableArrayList(tourLogs);
        for(TourLog log : databaseTourLogs){
            log.setTotalTime(log.getTotalTime()/60);
        }
        tourLogTable.setItems(databaseTourLogs);
    }

    public void writeToDownloads(String dest, Object jsonObject){
        String home = System.getProperty("user.home");
        File file = new File(home + dest);

        try{
            FileWriter fileWriter = new FileWriter(file);
            new ObjectMapper().writeValue(fileWriter, jsonObject);
            fileWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void exportTours(ActionEvent actionEvent){
        String dest = "/Downloads/tours-export.json";

        Object jsonObj = manager.getToursExport();
        if (jsonObj == null) {
            throw new RuntimeException("No Tours available");
        }

        writeToDownloads(dest, jsonObj);
    }

    public void exportTourLogs(ActionEvent actionEvent){
        Tour currentTour= (Tour) listTours.getSelectionModel().getSelectedItem();
        String dest =  "/Downloads/" + currentTour.getName() + "-logs-export.json";

        Object jsonObj = manager.getTourLogsExport(currentTour.getId());
        if (jsonObj == null) {
            throw new RuntimeException("No TourLogs available");
        }

        writeToDownloads(dest, jsonObj);
    }

    public void editTour(ActionEvent actionEvent) throws IOException {
        // Load the new FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("modificateTour.fxml"));
        ModificationTourController controller = new ModificationTourController((Tour) listTours.getSelectionModel().getSelectedItem());
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);

    }

    public void editTourLog(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("modificateTourLog.fxml"));
        ModificationTourLogController controller = new ModificationTourLogController((TourLog) tourLogTable.getSelectionModel().getSelectedItem());
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);
    }

    public void importJson(ActionEvent actionEvent) throws IOException {
        // Load the new FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("importJson.fxml"));
        JsonUploadController controller = new JsonUploadController();
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) myMenuBar.getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);

    }

    public void importJsonLogs(ActionEvent actionEvent) throws IOException {
        // Load the new FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("importJsonLogs.fxml"));
        JsonUploadController controller = new JsonUploadController((Tour) listTours.getSelectionModel().getSelectedItem());
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) myMenuBar.getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);

    }
}