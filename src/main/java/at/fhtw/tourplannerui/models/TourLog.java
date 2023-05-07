package at.fhtw.tourplannerui.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TourLog {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("creationTime")
    private Timestamp creationTime;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("difficulty")
    private Integer difficulty;
    @JsonProperty("totalTime")
    private Integer totalTime;      //seconds
    @JsonProperty("rating")
    private Integer rating;
}

