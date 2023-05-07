package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.TourLog;
import javafx.event.ActionEvent;
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

public class ModificationTourLogController implements Initializable {
    public TextField editDuration;
    public TextField editDifficulty;
    public TextField editRating;
    public TextArea editComment;
    private TourLog currentTourLog;

    public ModificationTourLogController(TourLog currentTourLog) {
        this.currentTourLog = currentTourLog;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editDuration.setText(Integer.toString(currentTourLog.getTotalTime()));
        editDifficulty.setText(Integer.toString(currentTourLog.getDifficulty()));
        editRating.setText(Integer.toString(currentTourLog.getRating()));
        editComment.setText(currentTourLog.getComment());
    }

    public void quitEditing(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainWindow.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 1600, 800);

        Parent root = fxmlLoader.load();

        // Create a new Scene with the new FXML file
        Scene scene = new Scene(root);

        // Get the Stage object from the current Scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Set the new Scene on the Stage
        stage.setScene(scene);
    }

    public void saveTourLog(ActionEvent actionEvent){
        //TODO save after editing
    }
}
