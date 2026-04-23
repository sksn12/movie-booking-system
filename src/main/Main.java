package main;

import java.util.Scanner;

import exception.LoginFailedException;
import member.MemberService;
import movie.MovieService;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MemberService memberService = new MemberService();
        MovieService movieService = new MovieService();

        while (true) {
            try {
                System.out.print("아이디를 입력해주세요 : ");
                String memberId = sc.nextLine();

                System.out.print("비밀번호를 입력해주세요 : ");
                String password = sc.nextLine();

                memberService.login(memberId, password);
            } catch (LoginFailedException loginFailedException) {
                System.out.println(loginFailedException.getMessage());
                continue;
            }

            break;
        }

        int condition;

        do {
            printInitMenu();
            System.out.print("원하는 기능의 번호를 입력해주세요 : ");
            condition = Integer.parseInt(sc.nextLine());

            switch (condition) {
                case 1:
                    showMovieList();
                    break;
                case 2:
                    showBookList();
                    break;
                case 3:
                    break;

                default:
                    System.out.println("잘못된 번호입니다.");
                    break;
            }

        } while (condition != 3);
    }

    public static void printInitMenu() {
        System.out.println("=======================");
        System.out.println("1. 상영중인 영화 조회 / 예약");
        System.out.println("2. 예매 내역 조회");
        System.out.println("3. 나가기");
        System.out.println("=======================");
    }

    public static void showMovieList() {

    }

    public static void showBookList() {

    }
}
