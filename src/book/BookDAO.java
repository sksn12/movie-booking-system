package book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.FileUtil;

public class BookDAO {

	private DataRepository dataRepository;

	public BookDAO() {
		dataRepository = DataRepository.getInstance();
	}

	public void readBookData() {
		dataRepository.setBookList(
				FileUtil.readLines(
						FilePath.BOOK_FILE_PATH)
						.stream()
						.map(s -> {
							String[] tokens = s.split("/");
							List<String> seatList = Arrays.asList(tokens[5].split(FilePath.FILE_DELIMITER));

							return new BookDTO(
									tokens[0],
									tokens[1],
									tokens[2],
									tokens[3],
									Integer.parseInt(tokens[4]),
									seatList,
									LocalDateTime.parse(tokens[6]),
									LocalDateTime.parse(tokens[7]),
									Integer.parseInt(tokens[8]),
									Boolean.parseBoolean(tokens[9]));
						}).collect(Collectors.toList()));
	}

	// public BookDTO findByBookId(String bookId) {
	// return dataRepository.getBookList()
	// .stream()
	// .filter(book -> book.getBookId().equals(bookId))
	// .findFirst()
	// .orElse(null);
	// }

	// public List<BookDTO> findByMemberId(String memberId) {
	// return dataRepository.getBookList()
	// .stream()
	// .filter(book -> book.getMemberId().equals(memberId))
	// .sorted(Comparator.comparing(BookDTO::getScreeningTime).reversed())
	// .toList();
	// }
}
