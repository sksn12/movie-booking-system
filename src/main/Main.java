package main;

import java.util.Arrays;

import book.BookDAO;
import common.DataRepository;
import member.MemberDTO;
import movie.MovieDAO;
import movie.MovieService;
import movie.PriceType;

public class Main {

    public static void main(String[] args) {
        DataRepository dataRepository = DataRepository.getInstance();
        MovieDAO movieDAO = new MovieDAO();
        MovieService movieService = new MovieService(movieDAO);
        movieService.readAllMovies();

        BookDAO bookDAO = new BookDAO();
        bookDAO.readBookData();

        MemberDTO member = MemberDTO.builder()
                .memberId("tsc")
                .password("tsc")
                .build();

        dataRepository.setLoginMember(member);
        bookDAO.book("20260416_1_01", "2026-04-16", Arrays.asList("A3"), PriceType.MORNING_PRICE);
    }
}
