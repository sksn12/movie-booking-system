package movie;

import java.util.List;

public class MovieService {

    private MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
    }

    public List<MovieDTO> getMoviesByDate(String date) {
        return movieDAO.findMoviesByDate(date);
    }
}
