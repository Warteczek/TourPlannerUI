package at.fhtw.tourplannerui;

import at.fhtw.tourplannerui.models.Tour;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ButtonMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.impl.NodeQueryImpl;
import org.testfx.util.NodeQueryUtils;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        //given
        ListView toursListBefore = (ListView) robot.lookup("#listTours").queryAll().iterator().next();
        int tourlistBeforeSize = toursListBefore.getItems().size();

        Tour tour = Tour.builder()
                .name("TestTour7")
                .description("description")
                .from("Vienna")
                .to("Salzburg")
                .build();

        //when
        robot.clickOn("#addTourTab");
        robot.clickOn("#addNameTour");
        robot.write(tour.getName());
        robot.clickOn("#addDescriptionTour");
        robot.write(tour.getDescription());
        robot.clickOn("#addStartLocation");
        robot.write(tour.getFrom());
        robot.clickOn("#addDestinationTour");
        robot.write(tour.getTo());
        robot.clickOn("#addTourButton");
        robot.sleep(1000);

        FxAssert.verifyThat("#listTours", NodeMatchers.isNotNull());
        robot.clickOn("#listTours");
        ListView toursList = (ListView) robot.lookup("#listTours").queryAll().iterator().next();

        WaitForAsyncUtils.waitForFxEvents();

        //then
        assertEquals(tourlistBeforeSize + 1, toursList.getItems().size());
    }

    @Test
    void canDeleteTour(FxRobot robot) {
        //given
        ListView toursListBefore = (ListView) robot.lookup("#listTours").queryAll().iterator().next();
        int tourlistBeforeSize = toursListBefore.getItems().size();

        List<Tour> tours = (List<Tour>) toursListBefore.getItems().stream().collect(Collectors.toList());
        Tour tour = (Tour) tours.get(tourlistBeforeSize-1);

        //when
        robot.clickOn("#deleteTourTab");
        robot.clickOn(tour.getName());
        robot.sleep(1000);
        robot.clickOn("#deleteToursButton");

        robot.clickOn("#listTours");
        ListView toursList = (ListView) robot.lookup("#listTours").queryAll().iterator().next();

        WaitForAsyncUtils.waitForFxEvents();

        //then
        assertEquals(tourlistBeforeSize - 1, toursList.getItems().size());
    }

    @Test
    void canSearchTour(FxRobot robot) {
        //given
        ListView toursListBefore = (ListView) robot.lookup("#listTours").queryAll().iterator().next();
        int tourlistBeforeSize = toursListBefore.getItems().size();

        List<Tour> tours = (List<Tour>) toursListBefore.getItems().stream().collect(Collectors.toList());
        Tour tour = (Tour) tours.get(tourlistBeforeSize-1);

        //when
        robot.clickOn("#searchField");
        robot.write(tour.getName());
        robot.clickOn("#searchButton");
        robot.sleep(1000);

        robot.clickOn("#listTours");
        ListView toursList = (ListView) robot.lookup("#listTours").queryAll().iterator().next();
        List<Tour> toursFound = (List<Tour>) toursList.getItems().stream().collect(Collectors.toList());
        Tour tourFound = toursFound.get(0);

        WaitForAsyncUtils.waitForFxEvents();

        //then
        assertEquals(tour.getName(), tourFound.getName());
    }

    @Test
    void canClearSearchBar(FxRobot robot) {
        //when
        robot.clickOn("#searchField");
        robot.write("test");
        robot.clickOn("#clearButton");
        robot.sleep(1000);

        FxAssert.verifyThat("#searchField", (TextField textArea) -> {
            String text = textArea.getText();
            return text.isEmpty();
        });
    }

}