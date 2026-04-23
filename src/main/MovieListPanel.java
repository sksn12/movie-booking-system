package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import movie.MovieDTO;
import movie.PriceType;

public class MovieListPanel extends JPanel {

    private final DefaultTableModel model;
    private final Map<String, List<MovieDTO>> cache = new HashMap<>();
    private final String[] selectedDate;

    public MovieListPanel(boolean bookMode) {
        setBackground(AppContext.BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        LocalDate today = LocalDate.now();
        DateTimeFormatter keyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dispFmt = DateTimeFormatter.ofPattern("MM/dd(E)", Locale.KOREAN);
        selectedDate = new String[] { today.format(keyFmt) };

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppContext.BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        topBar.add(AppContext.lbl(bookMode ? "영화 예약" : "상영중인 영화", 20, true), BorderLayout.WEST);
        JButton back = AppContext.btn("← 뒤로", AppContext.BTN_GRAY);
        back.addActionListener(e -> Main.menu());
        topBar.add(back, BorderLayout.EAST);

        JPanel dateBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        dateBar.setBackground(AppContext.BG);
        ButtonGroup bg = new ButtonGroup();
        for (int i = 0; i <= 10; i++) {
            LocalDate d = today.plusDays(i);
            JToggleButton tb = makeTab(d.format(dispFmt), d.equals(today));
            tb.addActionListener(e -> {
                selectedDate[0] = d.format(keyFmt);
                loadTable();
            });
            bg.add(tb);
            dateBar.add(tb);
        }

        String[] cols = { "[ID]", "제목", "장르", "관", "시작", "종료", "시간", "가격" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppContext.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(AppContext.BG);
        center.add(dateBar, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        if (bookMode) {
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setBackground(AppContext.BG);
            bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            JButton reserveBtn = AppContext.btn("선택한 영화 예약 →", AppContext.ACCENT);
            reserveBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(AppContext.frame, "예약할 영화를 선택하세요.", "안내",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String movieId = (String) model.getValueAt(row, 0);
                List<MovieDTO> list = cache.getOrDefault(selectedDate[0], Collections.emptyList());
                list.stream().filter(m -> m.getMovieId().equals(movieId)).findFirst().ifPresent(m -> {
                    if (AppContext.isPast(m)) {
                        JOptionPane.showMessageDialog(AppContext.frame, "지난 상영은 예약할 수 없습니다.", "안내",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Main.seatSelect(m, selectedDate[0]);
                });
            });
            bottom.add(reserveBtn);
            add(bottom, BorderLayout.SOUTH);
        }

        loadTable();
    }

    private void loadTable() {
        model.setRowCount(0);
        List<MovieDTO> movies = cache.computeIfAbsent(selectedDate[0], d -> {
            List<MovieDTO> m = AppContext.movieService.getMoviesByDate(d);
            return m != null ? m : Collections.emptyList();
        });
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        for (MovieDTO m : movies) {
            PriceType pt = AppContext.priceType(m.getStartTime());
            model.addRow(new Object[] {
                    m.getMovieId(), m.getTitle(), m.getGenre(),
                    m.getTheaterNo() + "관",
                    m.getStartTime().format(tf),
                    m.getEndTime().format(tf),
                    m.getRunningTime() + "분",
                    AppContext.priceLabel(pt)
            });
        }
    }

    private boolean isPastById(String movieId) {
        try {
            String dp = movieId.split("_")[0];
            LocalDate d = LocalDate.parse(dp, DateTimeFormatter.ofPattern("yyyyMMdd"));
            List<MovieDTO> list = AppContext.dataRepo.getMovieMap()
                    .get(d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (list != null) {
                return list.stream().filter(m -> m.getMovieId().equals(movieId))
                        .findFirst().map(m -> m.getStartTime().isBefore(LocalDateTime.now())).orElse(false);
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void styleTable(JTable t) {
        t.setBackground(AppContext.CARD);
        t.setForeground(AppContext.TEXT);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setGridColor(new Color(50, 50, 70));
        t.setSelectionBackground(AppContext.ACCENT);
        t.setSelectionForeground(Color.WHITE);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(new Color(40, 40, 65));
        t.getTableHeader().setForeground(AppContext.MUTED);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jt, Object v, boolean sel, boolean foc, int row,
                    int col) {
                super.getTableCellRendererComponent(jt, v, sel, foc, row, col);
                String id = (String) jt.getModel().getValueAt(row, 0);
                boolean past = isPastById(id);
                setBackground(sel ? (past ? AppContext.DIM_BG : AppContext.ACCENT)
                        : (past ? AppContext.DIM_BG : AppContext.CARD));
                setForeground(past ? AppContext.DIM_FG : AppContext.TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    private JToggleButton makeTab(String text, boolean selected) {
        JToggleButton tb = new JToggleButton(text);
        tb.setSelected(selected);
        tb.setBackground(selected ? AppContext.ACCENT : new Color(40, 40, 65));
        tb.setForeground(AppContext.TEXT);
        tb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tb.setBorderPainted(false);
        tb.setFocusPainted(false);
        tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tb.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        tb.addItemListener(e -> tb.setBackground(tb.isSelected() ? AppContext.ACCENT : new Color(40, 40, 65)));
        return tb;
    }
}
