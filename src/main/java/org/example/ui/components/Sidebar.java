package org.example.ui.components;

import org.example.ui.theme.Theme;
import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {

    public Sidebar() {
        // Layout vertical para empilhar os botões
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(46, 49, 49)); // Aquela cor grafite do seu Dashboard
        setPreferredSize(new Dimension(220, 0)); // Largura fixa de 220px
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
    }

    /**
     * Adiciona um botão estilizado ao menu lateral
     */
    public void addMenuItem(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Ocupa largura total
        btn.setFont(Theme.LABEL_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(46, 49, 49));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 20, 0, 0));

        // Efeito de hover simples
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(60, 63, 65));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(46, 49, 49));
            }
        });

        btn.addActionListener(e -> action.run());

        add(btn);
        add(Box.createVerticalStrut(2)); // Espacinho entre botões
    }
}