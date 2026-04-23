package movie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import common.DataRepository;

public class MovieService {

    private MovieDAO movieDAO;
    private DataRepository dataRepository;

    public MovieService() {
        this.movieDAO = new MovieDAO();
        this.dataRepository = DataRepository.getInstance();
        readAllMovies();
    }

    private void readAllMovies() {
        movieDAO.readAllMovies();
    }

    public List<MovieDTO> getMoviesByDate(String date) {
        return dataRepository.getMovieMap().get(date);
    }

    public void readMoviesByWeek() {
        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<MovieDTO> movies = dataRepository.getMovieMap().get(date);
            for (MovieDTO movie : movies) {
                System.out.println(movie);
            }
            localDate = localDate.plusDays(1);
        }
    }

}
