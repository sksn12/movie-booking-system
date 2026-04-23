package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import exception.LoginFailedException;

public class LoginPanel extends JPanel {

    public LoginPanel() {
        setBackground(AppContext.BG);
        setLayout(new GridBagLayout());

        JPanel card = AppContext.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(44, 52, 44, 52));
        card.setPreferredSize(new Dimension(390, 430));

        JLabel title = AppContext.lbl("영화 예매 시스템", 22, true);
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub = AppContext.lbl("로그인하여 시작하세요", 13, false);
        sub.setForeground(AppContext.MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JTextField idField = AppContext.field();
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPasswordField pwField = new JPasswordField();
        pwField.setBackground(new Color(40, 40, 60));
        pwField.setForeground(AppContext.TEXT);
        pwField.setCaretColor(AppContext.TEXT);
        pwField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pwField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 120)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        pwField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel errLbl = AppContext.lbl(" ", 12, false);
        errLbl.setForeground(AppContext.RED);
        errLbl.setAlignmentX(CENTER_ALIGNMENT);

        JButton loginBtn = AppContext.btn("로그인", AppContext.ACCENT);
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        Runnable doLogin = () -> {
            try {
                AppContext.memberService.login(idField.getText().trim(), new String(pwField.getPassword()));
                Main.menu();
            } catch (LoginFailedException ex) {
                errLbl.setText(ex.getMessage());
                pwField.setText("");
            }
        };
        loginBtn.addActionListener(e -> doLogin.run());
        pwField.addActionListener(e -> doLogin.run());
        idField.addActionListener(e -> pwField.requestFocus());

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(32));
        card.add(AppContext.lbl("아이디", 12, false));
        card.add(Box.createVerticalStrut(5));
        card.add(idField);
        card.add(Box.createVerticalStrut(14));
        card.add(AppContext.lbl("비밀번호", 12, false));
        card.add(Box.createVerticalStrut(5));
        card.add(pwField);
        card.add(Box.createVerticalStrut(6));
        card.add(errLbl);
        card.add(Box.createVerticalStrut(18));
        card.add(loginBtn);

        add(card);
    }
}
