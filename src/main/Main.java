package main;

import java.util.List;

import book.BookDTO;
import book.BookService;
import common.DataRepository;
import exception.LoginFailedException;
import member.MemberDAO;
import member.MemberService;
import movie.MovieDAO;
import movie.MovieDTO;

public class Main {

	public static void main(String[] args) {
		DataRepository dataRepository = DataRepository.getInstance();
		BookService bookService = new BookService();
		bookService.readBookData();
		List<BookDTO> books = bookService.findByMemberId("tst");
		for (BookDTO book : books) {
			System.out.println(book);
		}
		// MovieDAO movieDAO = new MovieDAO(dataRepository);
		// movieDAO.readAllMovies();
		// List<MovieDTO> movies = movieDAO.getMoviesByDate("2026-04-25");
		// for (MovieDTO movie : movies) {
		// System.out.println(movie);
		// }
		// MemberDAO memberDAO = new MemberDAO();
		// MemberService memberService = new MemberService(memberDAO, dataRepository);
		// memberDAO.readMemberData();

		// memberService.login("admin", "admin");
		// memberService.logout();

		// try {
		// memberService.login("tst", "error");
		// } catch (LoginFailedException loginFailedException) {
		// System.out.println(loginFailedException.getMessage());
		// }
	}
}
