package movie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.FileUtil;

public class MovieDAO {
    private DataRepository dataRepository;

    public MovieDAO() {
        this.dataRepository = DataRepository.getInstance();
    }

    public void readAllMovies() {
        Map<String, List<String>> movieMap = FileUtil.readLinesFromDirectory(FilePath.MOVIE_DIR_PATH);
        dataRepository.setMovieMap(movieMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> entry.getValue().stream().map(line -> {
                            String[] parts = line.split(FilePath.FILE_DELIMITER);
                            return new MovieDTO(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                                    Integer.parseInt(parts[4]),
                                    LocalDateTime.parse(parts[5]), LocalDateTime.parse(parts[6]));
                        }).collect(Collectors.toList()))));
    }
}
