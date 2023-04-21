package at.fhtw.tourplannerui.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAlias;

@AllArgsConstructor
@Builder
@Getter @Setter
public class Tour {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonProperty("type")
    private String type;
    @JsonProperty("distance")
    private int distance;
    @JsonProperty("time")
    private float time;
    @JsonProperty("imgPath")
    private String imgPath;

    public Tour(String tourName) {
        this.name = tourName;
    }
    public Tour() {
    }

}
