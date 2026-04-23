package movie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovieService {

    private MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
        readAllMovies();
    }

    private void readAllMovies() {
        movieDAO.readAllMovies();
    }

    public List<MovieDTO> getMoviesByDate(String date) {
        return movieDAO.findMoviesByDate(date);
    }

    public void readMoviesByWeek() {
        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<MovieDTO> movies = movieDAO.findMoviesByDate(date);
            if (movies != null) {
                for (MovieDTO movie : movies) {
                    System.out.println(movie);
                }
            }
            localDate = localDate.plusDays(1);
        }
    }

}
