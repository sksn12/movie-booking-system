package common;

import member.MemberDTO;

public class SessionManager {
    private static SessionManager instance;

    private MemberDTO loginMember;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }

        return instance;
    }

    public MemberDTO getLoginMember() {
        return loginMember;
    }

    public void setLoginMember(MemberDTO loginMember) {
        this.loginMember = loginMember;
    }
}
