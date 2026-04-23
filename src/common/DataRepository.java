package common;

import book.BookDTO;
import lombok.Data;
import member.MemberDTO;
import movie.MovieDTO;

import java.util.List;
import java.util.Map;

@Data
public class DataRepository {

	private static DataRepository instance;

	private List<MemberDTO> memberList;
	private List<BookDTO> bookList;
	private Map<String, List<MovieDTO>> movieMap;

	private MemberDTO loginMember;

	private DataRepository() {
	}

	public static DataRepository getInstance() {
		if (instance == null) {
			instance = new DataRepository();
		}

		return instance;
	}
}
