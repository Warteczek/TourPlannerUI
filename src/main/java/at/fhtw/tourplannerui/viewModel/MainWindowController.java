package at.fhtw.tourplannerui.viewModel;

import at.fhtw.tourplannerui.businesslayer.TourPlannerManager;
import at.fhtw.tourplannerui.businesslayer.TourPlannerManagerFactory;
import at.fhtw.tourplannerui.models.Tour;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    public ListView listTours;
    public TextField searchField;
    private ObservableList<Tour> tourList;
    private Tour currentTour;
    private TourPlannerManager manager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        manager= TourPlannerManagerFactory.getManager();

        setUpListView();

        formatCells();

        setCurrentTour();

    }

    private void setUpListView(){
        tourList= FXCollections.observableArrayList();
        tourList.addAll(manager.getTours());
        listTours.setItems(tourList);
    }

    private void setCurrentTour(){
        listTours.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if((newValue!=null) && (oldValue!=newValue)){
                currentTour= (Tour) newValue;
            }
        }));

    }

    private void formatCells(){
        listTours.setCellFactory(param -> new ListCell<Tour>(){
            protected void updateItem(Tour item, boolean empty){
                super.updateItem(item, empty);

                if(empty || (item == null) || (item.getTourName() == null)){
                    setText(null);
                } else{
                    setText(item.getTourName());
                }
            }
        });
    }

    public void quitApplication(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void searchAction(ActionEvent actionEvent) {

        tourList.clear();

        List<Tour> tours= manager.searchTours(searchField.textProperty().getValue(), false);

        tourList.addAll(tours);
    }

    public void clearAction(ActionEvent actionEvent) {
        tourList.clear();
        searchField.textProperty().setValue("");

        List<Tour> tours = manager.getTours();
        tourList.addAll(tours);
    }
}