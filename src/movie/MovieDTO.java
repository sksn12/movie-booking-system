package movie;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class MovieDTO {

    private String movieId;
    private String title;
    private String genre;
    private int runningTime;
    private int theaterNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
