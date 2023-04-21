package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.viewModel.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.TourPlannerManagerFactory;
import at.fhtw.tourplannerui.models.Tour;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
        tourListDelete.clear();

        List<Tour> tours= manager.searchTours(searchField.textProperty().getValue(), false);

        tourList.addAll(tours);
        tourListDelete.addAll(tours);
    }

    public void clearAction(ActionEvent actionEvent) {
        tourList.clear();
        tourListDelete.clear();
        searchField.textProperty().setValue("");

        List<Tour> tours = manager.getTours();
        tourList.addAll(tours);
        tourListDelete.addAll(tours);
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