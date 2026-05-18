package org.example.ui.components;

import org.example.ui.theme.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(Theme.PRIMARY);
        setForeground(Color.WHITE);
        setFont(Theme.BUTTON_FONT);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito Hover (Item 13)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Theme.PRIMARY_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Theme.PRIMARY);
            }
        });
    }
}