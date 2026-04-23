package main;

import common.DataRepository;
import exception.LoginFailedException;
import member.MemberDAO;
import member.MemberService;

public class Main {

	public static void main(String[] args) {
		DataRepository dataRepository = DataRepository.getInstance();
		MemberDAO memberDAO = new MemberDAO();
		MemberService memberService = new MemberService(memberDAO, dataRepository);
		memberDAO.readMemberData();

		memberService.login("admin", "admin");
		memberService.logout();

		try {
			memberService.login("tst", "error");
		} catch (LoginFailedException loginFailedException) {
			System.out.println(loginFailedException.getMessage());
		}
	}
}
