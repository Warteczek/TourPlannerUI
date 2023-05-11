package at.fhtw.tourplannerui.view;

import at.fhtw.tourplannerui.Main;
import at.fhtw.tourplannerui.models.TourLog;
import at.fhtw.tourplannerui.viewModel.TourPlannerManager;
import at.fhtw.tourplannerui.viewModel.TourPlannerManagerFactory;
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
    private TourPlannerManager manager;

    public ModificationTourLogController(TourLog currentTourLog) {
        this.currentTourLog = currentTourLog;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        manager= TourPlannerManagerFactory.getManager();

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

    public void saveTourLog(ActionEvent actionEvent) throws IOException {
        currentTourLog.setComment(editComment.getText());

        Integer duration=0;

        int difficulty=0, rating=0;

        try {
            duration=Integer.parseInt(editDuration.getText());
        } catch (NumberFormatException e) {
            editDuration.setText("Please put in a number");
            return;
        }

        try {
            difficulty=Integer.parseInt(editDifficulty.getText());
        } catch (NumberFormatException e) {
            editDifficulty.setText("Please put in a number");
            return;
        }

        try {
            rating=Integer.parseInt(editRating.getText());
        } catch (NumberFormatException e) {
            editRating.setText("Please put in a number");
            return;
        }

        if(rating>10 || rating<0){
            editRating.setText("Not a valid number");
            return;
        }
        if(difficulty>10 || difficulty<0){
            editDifficulty.setText("Not a valid number");
            return;
        }

        duration=duration*60;

        currentTourLog.setTotalTime(duration);
        currentTourLog.setDifficulty(difficulty);
        currentTourLog.setRating(rating);


        manager.saveTourLog(currentTourLog);
        quitEditing(actionEvent);
    }
}
