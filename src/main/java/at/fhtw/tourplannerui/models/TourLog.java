package at.fhtw.tourplannerui.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TourLog {
    private Long id;
    private Timestamp creationTime;
    private String comment;
    private Integer difficulty;
    private Integer totalTime;      //seconds
    private Integer rating;
}

