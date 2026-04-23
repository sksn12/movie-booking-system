package book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.SessionManager;
import exception.BookCancelException;
import exception.MovieNotSelectableException;
import movie.MovieDTO;
import movie.PriceType;

public class BookService {

    private BookDAO bookDAO;
    private DataRepository dataRepository;
    private SessionManager sessionManager;

    public BookService() {
        bookDAO = new BookDAO();
        dataRepository = DataRepository.getInstance();
        sessionManager = SessionManager.getInstance();
    }

    public List<BookDTO> findByMemberId(String memberId) {
        return bookDAO.findByMemberId(memberId);
    }

    public synchronized String book(String movieId, String date, List<String> seatList, PriceType priceType) {
        List<String> seats = bookDAO.findSeatsByMovieAndDate(movieId, date);

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

        bookDAO.updateSeats(movieId, date, updatedSeats);

        int sqeunceNo = bookDAO.findAll().size() + 1;

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
                .memberId(sessionManager.getLoginMember().getMemberId())
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

        bookDAO.save(bookDTO);

        return bookId;
    }

    public synchronized void cancelBook(String bookId) {
        List<BookDTO> bookList = bookDAO.findAll();
        List<BookDTO> updatedBookList = bookList.stream().map(b -> {
            if (b.getBookId().equals(bookId)) {
                b.setCanceled(true);
            }
            return b;
        }).collect(Collectors.toList());

        BookDTO book = updatedBookList.stream()
                .filter(b -> b.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);

        if (book == null) {
            throw new BookCancelException("없는 예약 id입니다.");
        }

        String date = book.getScreeningTime().toLocalDate().toString();
        String movieId = book.getMovieId();
        List<String> seatList = book.getSeatList();

        List<String> seats = bookDAO.findSeatsByMovieAndDate(movieId, date);

        String[][] seatArray = seats.stream().map(s -> s.split(FilePath.FILE_DELIMITER)).toArray(String[][]::new);

        for (String seat : seatList) {
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;

            seatArray[row][col] = "o";
        }

        List<String> updatedSeats = Arrays.stream(seatArray)
                .map(row -> String.join(FilePath.FILE_DELIMITER, row))
                .collect(Collectors.toList());

        bookDAO.updateSeats(movieId, date, updatedSeats);

        bookDAO.updateAll(updatedBookList);
    }
}
