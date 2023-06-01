package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.models.TourLog;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManager;
import at.fhtw.tourplannerui.viewModel.tourInformation.TourInformationManagerFactory;
import at.fhtw.tourplannerui.viewModel.tourLogs.TourLogManager;
import at.fhtw.tourplannerui.viewModel.tourLogs.TourLogManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class TourLogsController implements Initializable {

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
    public Label errorNoSelection;
    private Tour currentTour;
    private TourLogManager manager;
    private TourInformationController infoController;
    private ObservableList<TourLog> databaseTourLogs;


    public TourLogsController(Tour currentTour, TourInformationController infoController) {
        this.currentTour = currentTour;
        this.infoController = infoController;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manager= TourLogManagerFactory.getManager();

        //initialize TableView
        tourLogTableID.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("id"));
        tourLogTableCreationTime.setCellValueFactory(new PropertyValueFactory<TourLog, Timestamp>("creationTime"));
        tourLogTableComment.setCellValueFactory(new PropertyValueFactory<TourLog, String>("comment"));
        tourLogTableDifficulty.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("difficulty"));
        tourLogTableTotalTime.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("totalTime"));
        tourLogTableRating.setCellValueFactory(new PropertyValueFactory<TourLog, Integer>("rating"));

        List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
        databaseTourLogs=FXCollections.observableArrayList(tourLogs);
        for(TourLog log : databaseTourLogs){
            log.setTotalTime(log.getTotalTime()/60);
        }
        tourLogTable.setItems(databaseTourLogs);

        calculateChildFriendlinessAndPopularity();

        databaseTourLogs.addListener(new ListChangeListener<TourLog>() {
            @Override
            public void onChanged(Change<? extends TourLog> change) {
                calculateChildFriendlinessAndPopularity();
            }
        });
    }
    public void addTourLog(){

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
        databaseTourLogs.clear();
        databaseTourLogs.addAll(tourLogs);
        for(TourLog log : databaseTourLogs){
            log.setTotalTime(log.getTotalTime()/60);
        }
        tourLogTable.setItems(databaseTourLogs);

        addTourLogComment.textProperty().setValue("");
        addTourLogRating.textProperty().setValue("");
        addTourLogDifficulty.textProperty().setValue("");
        addTourLogDuration.textProperty().setValue("");
    }

    public void deleteTourLogAction(ActionEvent actionEvent) {
        if(tourLogTable.getSelectionModel().getSelectedItem()==null){
            errorNoSelection.setText("Please select Tour-Log");
            return;
        }
        TourLog selectedTourLog = (TourLog) tourLogTable.getSelectionModel().getSelectedItem();
        manager.deleteTourLog(selectedTourLog.getId());

        //load Tour Logs
        List<TourLog> tourLogs= manager.getTourLogs(currentTour.getId());
        databaseTourLogs.clear();
        databaseTourLogs.addAll(tourLogs);
        for(TourLog log : databaseTourLogs){
            log.setTotalTime(log.getTotalTime()/60);
        }
        tourLogTable.setItems(databaseTourLogs);
    }

    public void editTourLog(ActionEvent actionEvent) throws IOException {
        if(tourLogTable.getSelectionModel().getSelectedItem()==null){
            errorNoSelection.setText("Please select Tour-Log");
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("modificateTourLog.fxml"));
        ModificationTourLogController controller = new ModificationTourLogController((TourLog) tourLogTable.getSelectionModel().getSelectedItem());
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);
        String cssFile = getClass().getResource("/at/fhtw/tourplannerui/custom-style.css").toExternalForm();
        scene.getStylesheets().add(cssFile);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);
    }

    private void calculateChildFriendlinessAndPopularity(){
        Integer popularityCount=0;
        double difficultySum=0.0;


        for (TourLog tourLog:databaseTourLogs) {
            difficultySum=difficultySum+tourLog.getDifficulty();
            popularityCount=popularityCount+1;
        }

        if(infoController.infoDistance.getText().equals("Could not load distance") || infoController.infoTime.getText().equals("Could not load time")){
            infoController.setChildFriendliness("Not sure");
        }else{
            double averageDifficulty=difficultySum/popularityCount;
            String distanceString = infoController.infoDistance.getText().substring(0, infoController.infoDistance.getText().length() - 3);
            if(averageDifficulty>5.0 || Double.parseDouble(distanceString)>500.0){
                infoController.setChildFriendliness("No");
            }else if(popularityCount==0){
                infoController.setChildFriendliness("Not sure");
            }else{
                infoController.setChildFriendliness("Yes");
            }
        }
        infoController.setPopularity(popularityCount);
    }
}
