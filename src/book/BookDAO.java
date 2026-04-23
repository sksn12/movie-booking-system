package book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.FileUtil;
import exception.BookCancelException;
import exception.MovieNotSelectableException;
import movie.MovieDTO;
import movie.PriceType;

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

    public synchronized String book(String movieId, String date, List<String> seatList, PriceType priceType) {
        List<String> seats = FileUtil.readLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt");

        String[][] seatArray = seats.stream().map(s -> s.split(FilePath.FILE_DELIMITER)).toArray(String[][]::new);

        MovieDTO movie = dataRepository.getMovieMap().get(date).stream()
                .filter(m -> m.getMovieId().equals(movieId))
                .findFirst()
                .orElse(null);

        if (movie == null) {
            throw new MovieNotSelectableException("없는 영화입니다.");
        }

        for (String seat : seatList) {
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;

            if (seatArray[row][col].equals("x")) {
                return "";
            }

            seatArray[row][col] = "x";
        }

        List<String> updatedSeats = Arrays.stream(seatArray)
                .map(row -> String.join(FilePath.FILE_DELIMITER, row))
                .collect(Collectors.toList());

        FileUtil.writeLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt",
                updatedSeats);

        int sqeunceNo = dataRepository.getBookList().size() + 1;

        String bookId = String.format("%08d", sqeunceNo);

        int totalPrice = 0;

        switch (priceType) {
            case MORNING_PRICE:
                totalPrice = seatList.size() * 10000;
                break;
            case GENERAL_PRICE:
                totalPrice = seatList.size() * 15000;
                break;
            case NIGHT_PRICE:
                totalPrice = seatList.size() * 12000;
                break;
        }

        BookDTO bookDTO = BookDTO.builder()
                .bookId(bookId)
                .memberId(dataRepository.getLoginMember().getMemberId())
                .movieId(movieId)
                .movieTitle(movie.getTitle())
                .theaterNo(movie.getTheaterNo())
                .seatList(seatList)
                .screeningTime(movie.getStartTime())
                .bookedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .isCanceled(false)
                .build();

        System.out.println(bookDTO);

        dataRepository.getBookList().add(bookDTO);

        FileUtil.appendLine(FilePath.BOOK_FILE_PATH, bookDTO.toString());

        return bookId;
    }

    public synchronized void cancelBook(String bookId) {
        dataRepository.setBookList(dataRepository.getBookList()
                .stream()
                .map(b -> {
                    if (b.getBookId().equals(bookId)) {
                        b.setCanceled(true);
                    }
                    return b;
                }).collect(Collectors.toList()));

        BookDTO book = dataRepository.getBookList()
                .stream()
                .filter(b -> b.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (book == null) {
            throw new BookCancelException("없는 예약 id입니다.");
        }

        String date = book.getScreeningTime().toLocalDate().toString();
        String movieId = book.getMovieId();
        List<String> seatList = book.getSeatList();

        List<String> seats = FileUtil.readLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt");

        String[][] seatArray = seats.stream().map(s -> s.split(FilePath.FILE_DELIMITER)).toArray(String[][]::new);

        for (String seat : seatList) {
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;

            seatArray[row][col] = "o";
        }

        List<String> updatedSeats = Arrays.stream(seatArray)
                .map(row -> String.join(FilePath.FILE_DELIMITER, row))
                .collect(Collectors.toList());

        FileUtil.writeLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt",
                updatedSeats);

        List<String> newBookList = dataRepository.getBookList()
                .stream()
                .map(b -> b.toString())
                .collect(Collectors.toList());

        FileUtil.writeLines(FilePath.BOOK_FILE_PATH, newBookList);

    }
}
