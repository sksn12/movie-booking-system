package main;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import movie.MovieDTO;

public class Main {

    public static void main(String[] args) {
        AppContext.init();
        SwingUtilities.invokeLater(() -> {
            AppContext.frame = new JFrame("영화 예매 시스템");
            AppContext.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            AppContext.frame.setSize(960, 660);
            AppContext.frame.setLocationRelativeTo(null);
            AppContext.frame.setMinimumSize(new Dimension(820, 580));

            AppContext.cl = new CardLayout();
            AppContext.root = new JPanel(AppContext.cl);
            AppContext.root.setBackground(AppContext.BG);

            AppContext.frame.setContentPane(AppContext.root);
            AppContext.frame.setVisible(true);

            login();
        });
    }

    static void login() {
        AppContext.push("LOGIN", new LoginPanel());
    }

    static void menu() {
        AppContext.push("MENU", new MainMenuPanel());
    }

    static void movieList(boolean bookMode) {
        AppContext.push("MOVIES", new MovieListPanel(bookMode));
    }

    static void seatSelect(MovieDTO movie, String date) {
        AppContext.push("SEAT", new SeatSelectionPanel(movie, date));
    }

    static void myBookings() {
        AppContext.push("BOOKS", new MyBookingsPanel());
    }
}
