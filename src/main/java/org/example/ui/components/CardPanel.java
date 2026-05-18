package org.example.ui.components;

import org.example.ui.theme.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardPanel extends JPanel {

    public CardPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.SURFACE); // Branco

        // Borda composta: uma linha cinza bem fininha + margem interna de 20px
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
    }
}