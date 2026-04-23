package book;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
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

}
