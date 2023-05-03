package at.fhtw.tourplannerui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.ButtonMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.impl.NodeQueryImpl;
import org.testfx.util.NodeQueryUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class UITests {
    private Parent root = null;
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage primaryStage) {
        try {
            root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // bootstrap "window" named stage
        primaryStage.setTitle("Test Window");

        // set scene into stage in defined size
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);

        primaryStage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */

    @Test
    void canAddTour(FxRobot robot) {
        robot.clickOn("#addTourTab");

        robot.sleep(500);

        robot.clickOn("#addNameTour");
        robot.write("TestTour1");

        robot.sleep(500);

        robot.clickOn("#addDescriptionTour");
        robot.write("description");

        robot.sleep(500);

        robot.clickOn("#addStartLocation");
        robot.write("Vienna");

        robot.sleep(500);

        robot.clickOn("#addDestinationTour");
        robot.write("Salzburg");

        robot.sleep(500);

        robot.clickOn("#addTourButton");

        robot.sleep(1000);
    }

}