package org.example.ui.components;

import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Topbar extends JPanel {
    public Topbar(String title) {
        setLayout(new BorderLayout());
        setBackground(Theme.SURFACE);
        setPreferredSize(new Dimension(0, 70));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                new EmptyBorder(0, 25, 0, 25)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftPanel.setOpaque(false);

        // LOGO
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/assets/CDS_Logo.png")
            );

            Image scaled = icon.getImage().getScaledInstance(
                    110, // largura
                    50,  // altura
                    Image.SCALE_SMOOTH
            );
            JLabel logo = new JLabel(new ImageIcon(scaled));
            leftPanel.add(logo);
        } catch (Exception e) {
            System.out.println("Logo não encontrada.");
        }

        // TÍTULO
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.TITLE_FONT);
        lblTitle.setForeground(Theme.PRIMARY);
        leftPanel.add(lblTitle);
        add(leftPanel, BorderLayout.WEST);
    }
}