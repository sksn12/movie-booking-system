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

    public List<BookDTO> findAll() {
        return dataRepository.getBookList();
    }

    public List<String> findSeatsByMovieAndDate(String movieId, String date) {
        return FileUtil.readLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt");
    }

    public void updateSeats(String movieId, String date, List<String> updatedSeats) {
        FileUtil.writeLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt", updatedSeats);
    }

    public void save(BookDTO bookDTO) {
        dataRepository.getBookList().add(bookDTO);
        FileUtil.appendLine(FilePath.BOOK_FILE_PATH, bookDTO.toString());
    }

    public void updateAll(List<BookDTO> bookList) {
        dataRepository.setBookList(bookList);
        List<String> newBookListStr = bookList.stream()
                .map(BookDTO::toString)
                .collect(Collectors.toList());
        FileUtil.writeLines(FilePath.BOOK_FILE_PATH, newBookListStr);
    }
}
