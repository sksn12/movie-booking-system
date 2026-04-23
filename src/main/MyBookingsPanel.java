package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import book.BookDTO;
import exception.BookCancelException;

public class MyBookingsPanel extends JPanel {

    private static final String[] COLS = { "예매번호", "영화명", "관", "좌석", "상영 시간", "금액", "상태" };
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MyBookingsPanel() {
        setBackground(AppContext.BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppContext.BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        topBar.add(AppContext.lbl("예매 내역 조회", 20, true), BorderLayout.WEST);
        JButton back = AppContext.btn("← 뒤로", AppContext.BTN_GRAY);
        back.addActionListener(e -> Main.menu());
        topBar.add(back, BorderLayout.EAST);

        String memberId = AppContext.repo.getLoginMember().getMemberId();
        List<BookDTO> all = AppContext.bookService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();

        List<BookDTO> upcoming = all.stream()
                .filter(b -> !b.isCanceled() && b.getScreeningTime().isAfter(now))
                .collect(Collectors.toList());
        List<BookDTO> past = all.stream()
                .filter(b -> b.isCanceled() || b.getScreeningTime().isBefore(now))
                .collect(Collectors.toList());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(AppContext.BG);
        tabs.setForeground(AppContext.TEXT);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("관람 예정 (" + upcoming.size() + ")", buildTab(upcoming, true));
        tabs.addTab("지난 관람 (" + past.size() + ")", buildTab(past, false));

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTab(List<BookDTO> list, boolean canCancel) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(AppContext.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        DefaultTableModel model = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (BookDTO b : list) {
            String status;

            if (b.isCanceled()) {
                status = "취소 완료";
            } else if (LocalDateTime.now().isAfter(b.getScreeningTime())) {
                status = "-";
            } else {
                status = "예정";
            }

            model.addRow(new Object[] {
                    b.getBookId(),
                    b.getMovieTitle(),
                    b.getTheaterNo() + "관",
                    String.join(", ", b.getSeatList()),
                    b.getScreeningTime().format(DT_FMT),
                    String.format("%,d원", b.getTotalPrice()),
                    status
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(AppContext.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 70)));

        panel.add(scroll, BorderLayout.CENTER);

        if (canCancel) {
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setBackground(AppContext.BG);
            JButton cancelBtn = AppContext.btn("선택한 예매 취소", AppContext.RED);
            cancelBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(AppContext.frame,
                            "취소할 예매를 선택하세요.", "안내", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String bookId = (String) model.getValueAt(row, 0);
                String title = (String) model.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(AppContext.frame,
                        "정말로 [" + title + "] 예매를 취소하시겠습니까?\n예매번호: " + bookId,
                        "예매 취소 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION)
                    return;
                try {
                    AppContext.bookService.cancelBook(bookId);
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(AppContext.frame,
                            "예매가 취소되었습니다.\n예매번호: " + bookId, "취소 완료", JOptionPane.INFORMATION_MESSAGE);
                } catch (BookCancelException ex) {
                    JOptionPane.showMessageDialog(AppContext.frame,
                            ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            });
            bottom.add(cancelBtn);
            panel.add(bottom, BorderLayout.SOUTH);
        }

        return panel;
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
                setBackground(sel ? AppContext.ACCENT : AppContext.CARD);
                setForeground(sel ? Color.WHITE : AppContext.TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
