package book;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import movie.PriceType;

public class BookService {

    private BookDAO bookDAO;
    private DataRepository dataRepository;

    public BookService() {
        bookDAO = new BookDAO();
        dataRepository = DataRepository.getInstance();
    }

    public void readBookData() {
        bookDAO.readBookData();
    }

    public List<BookDTO> findByMemberId(String memberId) {
        return dataRepository.getBookList()
                .stream()
                .filter(book -> book.getMemberId().equals(memberId))
                .sorted(Comparator.comparing(BookDTO::getScreeningTime).reversed())
                .collect(Collectors.toList());
    }

    public String book(String movieId, String date, List<String> seatList, PriceType priceType) {
        return bookDAO.book(movieId, date, seatList, priceType);
    }

    public void cancelBook(String bookId) {
        bookDAO.cancelBook(bookId);
    }
}
