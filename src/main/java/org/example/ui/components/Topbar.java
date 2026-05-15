package org.example.ui.components;

import org.example.ui.theme.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Topbar extends JPanel {

    public Topbar(String title) {
        setLayout(new BorderLayout());
        setBackground(Theme.SURFACE);
        setPreferredSize(new Dimension(0, 70)); // Altura fixa de 70px
        setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.TITLE_FONT);
        lblTitle.setForeground(Theme.PRIMARY); // Roxo do seu tema

        add(lblTitle, BorderLayout.WEST);

        // Linha decorativa embaixo da barra
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
    }
}