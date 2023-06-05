package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.TourLog;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManager;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManagerFactory;
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
    public MenuBar myMenuBar;
    public VBox containerInfoAndLogs;
    public Label errorNoTourSelected;
    public Label errorNoDeleteSelected;



    private ObservableList<Tour> tourList;
    private ObservableList<Tour> tourListDelete;
    private Tour currentTour;
    private TourPlannerManager manager;
    private TourInformationManager informationManager;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        manager= TourPlannerManagerFactory.getManager();
        informationManager = TourInformationManagerFactory.getManager();

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

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("tourInformation.fxml"));
                TourInformationController tourInformationController = new TourInformationController(currentTour);
                loader.setController(tourInformationController);
                Parent root = null; // load the FXML file
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                FXMLLoader loaderLogs = new FXMLLoader(Main.class.getResource("tourLogs.fxml"));
                TourLogsController tourLogsController = new TourLogsController(currentTour, tourInformationController);
                loaderLogs.setController(tourLogsController);
                Parent rootLogs = null; // load the FXML file
                try {
                    rootLogs = loaderLogs.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                containerInfoAndLogs.getChildren().clear();

                containerInfoAndLogs.getChildren().add(root);
                containerInfoAndLogs.getChildren().add(rootLogs);
            }
        }));

        deleteListTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("tourInformation.fxml"));
                TourInformationController tourInformationController = new TourInformationController(currentTour);
                loader.setController(tourInformationController);
                Parent root = null; // load the FXML file
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                FXMLLoader loaderLogs = new FXMLLoader(Main.class.getResource("tourLogs.fxml"));
                TourLogsController tourLogsController = new TourLogsController(currentTour, tourInformationController);
                loaderLogs.setController(tourLogsController);
                Parent rootLogs = null; // load the FXML file
                try {
                    rootLogs = loaderLogs.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                containerInfoAndLogs.getChildren().clear();

                containerInfoAndLogs.getChildren().add(root);
                containerInfoAndLogs.getChildren().add(rootLogs);
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
        if(deleteListTours.getSelectionModel().getSelectedItem()==null){
            errorNoDeleteSelected.setText("Please select Tour");
            return;
        }
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
            //load Tour Data
            String responseString=informationManager.getDistanceAndTime(currentTour);
            double distance_value = 0;
            double time = 0;

            try {
                JSONObject json_obj = new JSONObject(responseString);
                distance_value = json_obj.getDouble("distance");
                time = json_obj.getDouble("time");
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new Paragraph("Distance: " + (distance_value == 0 ? null : distance_value)));
            document.add(new Paragraph("Time: " + (time == 0 ? null : time)));
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
        if(listTours.getSelectionModel().getSelectedItem()==null){
            errorNoTourSelected.setText("Please select Tour");
            return;
        }
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