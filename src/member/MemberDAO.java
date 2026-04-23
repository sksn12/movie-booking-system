package member;

import java.util.List;
import java.util.stream.Collectors;

import common.DataRepository;
import common.FilePath;
import common.FileUtil;

public class MemberDAO {

	private DataRepository dataRepository;

	public MemberDAO() {
		this.dataRepository = DataRepository.getInstance();
	}

	public void readMemberData() {
		List<String> lines = FileUtil.readLines(FilePath.MEMBER_FILE_PATH);

		dataRepository.setMemberList(lines.stream().map(line -> {
			String[] parts = line.split(FilePath.FILE_DELIMITER);
			return new MemberDTO(parts[0], parts[1]);
		}).collect(Collectors.toList()));
	}

	public MemberDTO findMemberById(String memberId) {
		return dataRepository.getMemberList().stream()
				.filter(member -> member.getMemberId().equals(memberId))
				.findFirst()
				.orElse(null);
	}
}
