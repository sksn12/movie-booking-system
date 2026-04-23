package book;

import java.time.LocalDateTime;
import java.util.List;

import common.FilePath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookDTO {

    private String bookId;
    private String memberId;
    private String movieId;
    private String movieTitle;
    private int theaterNo;
    private List<String> seatList;
    private LocalDateTime screeningTime;
    private LocalDateTime bookedAt;
    private int totalPrice;
    private boolean isCanceled;

    @Override
    public String toString() {
        return String.format("%s/%s/%s/%s/%d/%s/%s/%s/%d/%s",
                bookId, memberId, movieId, movieTitle, theaterNo, String.join(FilePath.FILE_DELIMITER, seatList),
                screeningTime, bookedAt, totalPrice, isCanceled);
    }
}
