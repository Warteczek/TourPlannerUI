package at.fhtw.tourplannerui.models;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

public class Tour {

    @Getter @Setter public String tourName;
    @Getter @Setter public String description;
    @Getter @Setter public String startingLocation;
    @Getter @Setter public String targetLocation;
    @Getter @Setter public String transportType;
    @Getter @Setter public int distance;
    @Getter @Setter public float estimatedTime;
    @Getter @Setter public Image routeMap;

    public Tour(String tourName){
        this.tourName=tourName;
    }
}
