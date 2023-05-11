package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.Tour;
import at.fhtw.tourplannerui.viewModel.tourPlanner.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.tourPlanner.TourPlannerManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class JsonUploadController implements Initializable {
    private TourPlannerManager manager;
    private Tour currentTour;

    public JsonUploadController(Tour currentTour) {
        this.currentTour = currentTour;
    }

    public JsonUploadController() {
    }

    public Object getUpload(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json"));

        // Show the file chooser
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileReader reader = new FileReader(selectedFile)) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(reader, Object.class);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to read JSON file");
                alert.setContentText("An error occurred while reading the selected JSON file.");
                alert.showAndWait();
            }
        }
        return null;
    }

    public void importTour(ActionEvent actionEvent) throws IOException {
        manager.addTourFromJson(getUpload());
        quitFileUpload(actionEvent);
    }

    public void importTourLogs(ActionEvent actionEvent) throws IOException {
        manager.addTourLogFromJson(getUpload(), currentTour.getId());
        quitFileUpload(actionEvent);
    }

    public void quitFileUpload(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainWindow.fxml"));

        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manager= TourPlannerManagerFactory.getManager();
    }
}
