package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import book.BookDTO;
import lombok.Data;
import member.MemberDTO;
import movie.MovieDTO;

@Data
public class DataRepository {

    private static DataRepository instance;

    private List<MemberDTO> memberList;
    private List<BookDTO> bookList;
    private Map<String, List<MovieDTO>> movieMap;

    private DataRepository() {
        movieMap = new HashMap<>();
        memberList = new ArrayList<>();
        bookList = new ArrayList<>();
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }

        return instance;
    }
}
