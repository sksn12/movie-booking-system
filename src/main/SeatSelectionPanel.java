package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import common.FilePath;
import common.FileUtil;
import exception.MovieNotSelectableException;
import exception.PastMovieBookingException;
import exception.SeatAlreadyBookedException;
import movie.MovieDTO;
import movie.PriceType;

public class SeatSelectionPanel extends JPanel {

    private final List<String> selectedSeats = new ArrayList<>();
    private JLabel selectedLbl;
    private JLabel totalLbl;
    private JLabel errLbl;
    private PriceType pt;

    public SeatSelectionPanel(MovieDTO movie, String date) {
        setBackground(AppContext.BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 16, 30));

        pt = AppContext.priceType(movie.getStartTime());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        JPanel northArea = new JPanel();
        northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
        northArea.setBackground(AppContext.BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppContext.BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topBar.add(AppContext.lbl("좌석 선택 — " + movie.getTitle(), 18, true), BorderLayout.WEST);
        JButton back = AppContext.btn("← 뒤로", AppContext.BTN_GRAY);
        back.addActionListener(e -> Main.movieList(true));
        topBar.add(back, BorderLayout.EAST);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        info.setBackground(new Color(28, 28, 45));
        info.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 90)));
        info.add(AppContext.chip("장르", movie.getGenre()));
        info.add(AppContext.chip("상영관", movie.getTheaterNo() + "관"));
        info.add(AppContext.chip("시작", movie.getStartTime().format(fmt)));
        info.add(AppContext.chip("종료", movie.getEndTime().format(fmt)));
        info.add(AppContext.chip("1인 가격", AppContext.priceLabel(pt)));

        northArea.add(topBar);
        northArea.add(info);
        northArea.add(Box.createVerticalStrut(10));

        int rows = 10, cols = 20;

        String seatFilePath = FilePath.SEAT_DIR_PATH + date + "/" + movie.getMovieId() + ".txt";
        boolean[][] taken = loadSeatState(seatFilePath, rows, cols);

        JLabel screenLbl = new JLabel("▬▬▬▬▬▬▬▬▬  SCREEN  ▬▬▬▬▬▬▬▬▬", SwingConstants.CENTER);
        screenLbl.setForeground(new Color(180, 180, 220));
        screenLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        screenLbl.setAlignmentX(CENTER_ALIGNMENT);

        JPanel seatGrid = buildSeatGrid(rows, cols, taken);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        legend.setBackground(AppContext.BG);
        legend.add(legendItem(new Color(50, 50, 75), "선택 가능"));
        legend.add(legendItem(AppContext.ACCENT, "선택됨"));
        legend.add(legendItem(new Color(100, 40, 40), "예약됨"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(AppContext.BG);
        centerPanel.add(screenLbl);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(seatGrid);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(legend);

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBackground(AppContext.BG);
        scroll.getViewport().setBackground(AppContext.BG);
        scroll.setBorder(null);

        JPanel bottomCard = AppContext.card();
        bottomCard.setLayout(new BoxLayout(bottomCard, BoxLayout.Y_AXIS));
        bottomCard.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        selectedLbl = AppContext.lbl("선택된 좌석: 없음", 13, false);
        selectedLbl.setForeground(AppContext.MUTED);
        selectedLbl.setAlignmentX(LEFT_ALIGNMENT);

        totalLbl = AppContext.lbl("예상 금액: 0원", 15, true);
        totalLbl.setForeground(AppContext.GREEN);
        totalLbl.setAlignmentX(LEFT_ALIGNMENT);

        errLbl = AppContext.lbl(" ", 12, false);
        errLbl.setForeground(AppContext.RED);
        errLbl.setAlignmentX(LEFT_ALIGNMENT);

        JButton confirmBtn = AppContext.btn("예약 확정", AppContext.GREEN);
        confirmBtn.setForeground(new Color(10, 10, 20));
        confirmBtn.setAlignmentX(LEFT_ALIGNMENT);
        confirmBtn.addActionListener(e -> doBook(movie, date));

        bottomCard.add(selectedLbl);
        bottomCard.add(Box.createVerticalStrut(6));
        bottomCard.add(totalLbl);
        bottomCard.add(Box.createVerticalStrut(4));
        bottomCard.add(errLbl);
        bottomCard.add(Box.createVerticalStrut(10));
        bottomCard.add(confirmBtn);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(AppContext.BG);
        southWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        southWrapper.add(bottomCard, BorderLayout.CENTER);

        add(northArea, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.SOUTH);
    }

    private JPanel buildSeatGrid(int rows, int cols, boolean[][] taken) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(AppContext.BG);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(AppContext.BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.gridy = 0;
        gbc.gridx = 0;
        grid.add(new JLabel(""), gbc);
        for (int c = 0; c < cols; c++) {
            gbc.gridx = c + 1;
            JLabel colLbl = new JLabel(String.valueOf(c + 1), SwingConstants.CENTER);
            colLbl.setForeground(AppContext.MUTED);
            colLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            colLbl.setPreferredSize(new Dimension(40, 20));
            grid.add(colLbl, gbc);
        }

        for (int r = 0; r < rows; r++) {
            gbc.gridy = r + 1;
            gbc.gridx = 0;
            JLabel rowLbl = new JLabel(String.valueOf((char) ('A' + r)), SwingConstants.CENTER);
            rowLbl.setForeground(AppContext.MUTED);
            rowLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
            rowLbl.setPreferredSize(new Dimension(22, 36));
            grid.add(rowLbl, gbc);

            for (int c = 0; c < cols; c++) {
                final String seatId = String.valueOf((char) ('A' + r)) + (c + 1);
                JToggleButton btn = new JToggleButton(String.valueOf(c + 1));
                btn.setPreferredSize(new Dimension(40, 36));
                btn.setFont(new Font("SansSerif", Font.PLAIN, 10));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100), 1));
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setToolTipText(seatId);

                if (taken[r][c]) {
                    btn.setBackground(Color.RED);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(false);
                } else {
                    btn.setBackground(new Color(50, 50, 75));
                    btn.setForeground(AppContext.TEXT);
                    btn.addItemListener(ev -> {
                        if (btn.isSelected()) {
                            btn.setBackground(Color.GREEN);
                            btn.setForeground(Color.WHITE);
                            selectedSeats.add(seatId);
                        } else {
                            btn.setBackground(new Color(50, 50, 75));
                            btn.setForeground(Color.WHITE);
                            selectedSeats.remove(seatId);
                        }
                        updateSummary();
                    });
                }
                gbc.gridx = c + 1;
                grid.add(btn, gbc);
            }
        }
        wrapper.add(grid);
        return wrapper;
    }

    private boolean[][] loadSeatState(String filePath, int rows, int cols) {
        boolean[][] taken = new boolean[rows][cols];
        List<String> lines = FileUtil.readLines(filePath);
        for (int r = 0; r < Math.min(rows, lines.size()); r++) {
            String[] parts = lines.get(r).split(",");
            for (int c = 0; c < Math.min(cols, parts.length); c++) {
                taken[r][c] = "x".equals(parts[c].trim());
            }
        }
        return taken;
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            selectedLbl.setText("선택된 좌석: 없음");
            selectedLbl.setForeground(AppContext.MUTED);
            totalLbl.setText("예상 금액: 0원");
        } else {
            List<String> sorted = new ArrayList<>(selectedSeats);
            Collections.sort(sorted);
            selectedLbl.setText("선택된 좌석: " + String.join(", ", sorted));
            selectedLbl.setForeground(AppContext.TEXT);
            totalLbl.setText(String.format("예상 금액: %,d원  (%d석 × %s)",
                    (long) sorted.size() * AppContext.unitPrice(pt), sorted.size(), AppContext.priceLabel(pt)));
        }
        errLbl.setText(" ");
    }

    private void doBook(MovieDTO movie, String date) {
        if (selectedSeats.isEmpty()) {
            errLbl.setText("좌석을 1개 이상 선택해주세요.");
            return;
        }
        List<String> sorted = new ArrayList<>(selectedSeats);
        Collections.sort(sorted);
        int confirm = JOptionPane.showConfirmDialog(AppContext.frame,
                String.format("좌석 %s 을(를) 예약하시겠습니까?\n총 금액: %,d원",
                        String.join(", ", sorted), sorted.size() * AppContext.unitPrice(pt)),
                "예약 확인", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            String bookId = AppContext.bookService.book(movie.getMovieId(), date, sorted, pt);

            JOptionPane.showMessageDialog(AppContext.frame,
                    "예매가 완료되었습니다!\n예매 번호: " + bookId, "예매 완료", JOptionPane.INFORMATION_MESSAGE);
            Main.menu();
        } catch (MovieNotSelectableException ex) {
            errLbl.setText(ex.getMessage());
        } catch (SeatAlreadyBookedException ex) {
            errLbl.setText(ex.getMessage());
        } catch (PastMovieBookingException ex) {
            errLbl.setText(ex.getMessage());
        } catch (Exception ex) {
            errLbl.setText("예약 중 오류: " + ex.getMessage());
        }
    }

    private JPanel legendItem(Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setBackground(AppContext.BG);
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(16, 16));
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100)));
        JLabel lbl = AppContext.lbl(label, 12, false);
        lbl.setForeground(AppContext.MUTED);
        item.add(swatch);
        item.add(lbl);
        return item;
    }
}
