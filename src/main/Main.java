package main;

import java.util.ArrayList;
import java.util.List;

import book.BookDTO;
import common.DataRepository;
import exception.LoginFailedException;
import member.MemberDAO;
import member.MemberDTO;
import member.MemberService;
import movie.MovieDTO;

public class Main {
	public static void main(String[] args) {
		DataRepository dataRepository = DataRepository.getInstance();
		MemberDAO memberDAO = new MemberDAO();
		MemberService memberService = new MemberService(memberDAO, dataRepository);
		memberDAO.readMemberData();

		memberService.login("admin", "admin");
		System.out.println(dataRepository.getLoginMember());
		memberService.logout();
		System.out.println(dataRepository.getLoginMember());

		try {
			memberService.login("tst", "error");
		} catch (LoginFailedException loginFailedException) {
			System.out.println(loginFailedException.getMessage());
		}
	}
}
