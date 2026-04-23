package member;

import common.SessionManager;
import exception.LoginFailedException;

public class MemberService {

    private MemberDAO memberDAO;
    private SessionManager sessionManager;

    public MemberService() {
        memberDAO = new MemberDAO();
        sessionManager = SessionManager.getInstance();
        readMemberData();
    }

    private void readMemberData() {
        memberDAO.readMemberData();
    }

    public void login(String memberId, String password) {
        MemberDTO member = memberDAO.findMemberById(memberId);
        if (member != null && member.getPassword().equals(password)) {
            sessionManager.setLoginMember(member);
            System.out.println("정상적으로 로그인이 되었습니다.");
        } else {
            throw new LoginFailedException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public void logout() {
        sessionManager.setLoginMember(null);
    }
}
