package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel() {
        setBackground(AppContext.BG);
        setLayout(new GridBagLayout());

        JPanel card = AppContext.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(50, 64, 50, 64));
        card.setPreferredSize(new Dimension(430, 400));

        String id = AppContext.sessionManager.getLoginMember().getMemberId();
        JLabel welcome = AppContext.lbl("안녕하세요, " + id + "님!", 18, true);
        welcome.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 90));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JButton movieBtn = AppContext.btn("상영중인 영화 조회 / 예약", AppContext.ACCENT);
        movieBtn.setAlignmentX(CENTER_ALIGNMENT);
        movieBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JButton bookBtn = AppContext.btn("예매 내역 조회", new Color(34, 197, 94));
        bookBtn.setAlignmentX(CENTER_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JButton logoutBtn = AppContext.btn("로그아웃", new Color(100, 100, 130));
        logoutBtn.setAlignmentX(CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        movieBtn.addActionListener(e -> {
            Main.movieList(true);
        });
        bookBtn.addActionListener(e -> Main.myBookings());
        logoutBtn.addActionListener(e -> {
            AppContext.memberService.logout();
            AppContext.root.removeAll();
            Main.login();
        });

        card.add(welcome);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(28));
        card.add(movieBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(bookBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(logoutBtn);

        add(card);
    }
}
