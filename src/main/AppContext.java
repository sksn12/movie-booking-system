package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import book.BookDAO;
import book.BookService;
import common.DataRepository;
import common.SessionManager;
import member.MemberDAO;
import member.MemberService;
import movie.MovieDAO;
import movie.MovieDTO;
import movie.MovieService;
import movie.PriceType;

public class AppContext {

    static final Color BG = new Color(18, 18, 30);
    static final Color CARD = new Color(28, 28, 45);
    static final Color ACCENT = new Color(99, 102, 241);
    static final Color TEXT = new Color(240, 240, 255);
    static final Color MUTED = new Color(160, 160, 200);
    static final Color GREEN = new Color(52, 211, 153);
    static final Color RED = new Color(239, 68, 68);
    static final Color DIM_BG = new Color(35, 35, 50);
    static final Color DIM_FG = new Color(90, 90, 110);
    static final Color BTN_GRAY = new Color(80, 80, 110);

    static MemberService memberService;
    static MovieService movieService;
    static BookService bookService;
    static DataRepository dataRepo;
    static SessionManager sessionManager;

    static JFrame frame;
    static CardLayout cl;
    static JPanel root;

    static void init() {
        new MemberDAO().readMemberData();
        new MovieDAO().readAllMovies();
        new BookDAO().readBookData();

        memberService = new MemberService();
        movieService = new MovieService();
        bookService = new BookService();
        
        dataRepo = DataRepository.getInstance();
        sessionManager = SessionManager.getInstance();
    }

    static void go(String key) {
        cl.show(root, key);
    }

    static void push(String key, JPanel panel) {
        for (int i = 0; i < root.getComponentCount(); i++) {
            if (key.equals(root.getComponent(i).getName())) {
                root.remove(i);
                break;
            }
        }
        panel.setName(key);
        root.add(panel, key);
        go(key);
    }

    static JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(TEXT);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(bg.brighter());
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    static JLabel lbl(String t, int sz, boolean bold) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, sz));
        return l;
    }

    static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        return p;
    }

    static JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(new Color(40, 40, 60));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 120)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return f;
    }

    static PriceType priceType(LocalDateTime t) {
        LocalTime lt = t.toLocalTime();
        if (lt.isBefore(LocalTime.NOON))
            return PriceType.MORNING_PRICE;
        if (lt.isBefore(LocalTime.of(22, 0)))
            return PriceType.GENERAL_PRICE;
        return PriceType.NIGHT_PRICE;
    }

    static String priceLabel(PriceType pt) {
        switch (pt) {
            case MORNING_PRICE:
                return "10,000원";
            case GENERAL_PRICE:
                return "15,000원";
            case NIGHT_PRICE:
                return "12,000원";
            default:
                return "";
        }
    }

    static int unitPrice(PriceType pt) {
        switch (pt) {
            case MORNING_PRICE:
                return 10000;
            case GENERAL_PRICE:
                return 15000;
            case NIGHT_PRICE:
                return 12000;
            default:
                return 0;
        }
    }

    static boolean isPast(MovieDTO m) {
        return m.getStartTime().isBefore(LocalDateTime.now());
    }

    static JLabel chip(String key, String val) {
        JLabel l = new JLabel(key + ": " + val);
        l.setForeground(TEXT);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 120)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        l.setBackground(new Color(40, 40, 65));
        l.setOpaque(true);
        return l;
    }
}
