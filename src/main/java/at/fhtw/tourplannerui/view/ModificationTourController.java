package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.viewModel.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.TourPlannerManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ModificationTourController implements Initializable {
    public TextField editName;
    public TextField editTo;
    public TextField editFrom;
    public TextField editTransportType;
    public TextArea editDescription;
    private Tour currentTour;
    private TourPlannerManager manager;

    public ModificationTourController(Tour currentTour) {
        this.currentTour = currentTour;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manager= TourPlannerManagerFactory.getManager();

        editName.setText(currentTour.getName());
        editTo.setText(currentTour.getTo());
        editFrom.setText(currentTour.getFrom());
        editTransportType.setText(currentTour.getType());
        editDescription.setText(currentTour.getDescription());
    }

    public void quitEditing(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainWindow.fxml"));

        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);
    }

    public void saveTour(ActionEvent actionEvent) throws IOException {
        currentTour.setName(editName.getText());
        currentTour.setFrom(editFrom.getText());
        currentTour.setTo(editTo.getText());
        currentTour.setType(editTransportType.getText());
        currentTour.setDescription(editDescription.getText());

        manager.saveTour(currentTour);
        quitEditing(actionEvent);
    }
}
