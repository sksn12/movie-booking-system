package common;

import book.BookDTO;
import member.MemberDTO;
import movie.MovieDTO;

import java.util.List;
import java.util.Map;

public class DataRepository {

	private static DataRepository instance;

	private List<MemberDTO> memberList;
	private List<BookDTO> bookList;
	private Map<String, List<MovieDTO>> movieMap;

}
