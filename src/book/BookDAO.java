package book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.FileUtil;
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

    public synchronized boolean book(String movieId, String date, List<String> seatList, PriceType priceType) {
        List<String> seats = FileUtil.readLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt");

        String[][] seatArray = seats.stream().map(s -> s.split(FilePath.FILE_DELIMITER)).toArray(String[][]::new);

        for (String seat : seatList) {
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;

            if (seatArray[row][col].equals("x")) {
                return false;
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

        MovieDTO movie = dataRepository.getMovieMap().get(date).stream()
                .filter(m -> m.getMovieId().equals(movieId))
                .findFirst()
                .orElse(null);

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
                .screeningTime(LocalDateTime.now())
                .bookedAt(LocalDateTime.now())
                .totalPrice(totalPrice)
                .isCanceled(false)
                .build();

        System.out.println(bookDTO);

        dataRepository.getBookList().add(bookDTO);

        FileUtil.appendLine(FilePath.BOOK_FILE_PATH, bookDTO.toString());

        return true;
    }

    // public synchronized void cancelBook(String bookId) {
    // List<String> seats = FileUtil.readLines(FilePath.SEAT_DIR_PATH + date + "/" +
    // movieId + ".txt");

    // String[][] seatArray = seats.stream().map(s ->
    // s.split(FilePath.FILE_DELIMITER)).toArray(String[][]::new);

    // for (String seat : seatList) {
    // int row = seat.charAt(0) - 'A';
    // int col = Integer.parseInt(seat.substring(1)) - 1;

    // seatArray[row][col] = "o";
    // }

    // List<String> updatedSeats = Arrays.stream(seatArray)
    // .map(row -> String.join(FilePath.FILE_DELIMITER, row))
    // .collect(Collectors.toList());

    // FileUtil.writeLines(FilePath.SEAT_DIR_PATH + date + "/" + movieId + ".txt",
    // updatedSeats);
    // }

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
