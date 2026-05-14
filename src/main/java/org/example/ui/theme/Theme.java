package org.example.ui.theme;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public final class Theme {
    /*
     * CORES BASE
     */
    public static final Color BACKGROUND =
            new Color(245, 247, 250);

    public static final Color SURFACE =
            Color.WHITE;

    /*
     * CORES PRINCIPAIS
     */
    public static final Color PRIMARY =
            new Color(199, 108, 255);

    public static final Color PRIMARY_DARK =
            new Color(161, 57, 223);

    /*
     * TEXTO
     */
    public static final Color TEXT_PRIMARY =
            new Color(255, 255, 255);

    public static final Color TEXT_SECONDARY =
            new Color(214, 214, 214);

    /*
     * BORDAS
     */
    public static final Color BORDER =
            new Color(220, 220, 220);

    /*
     * FONTES
     */
    public static final Font TITLE_FONT =
            new Font("SansSerif", Font.BOLD, 30);

    public static final Font SUBTITLE_FONT =
            new Font("SansSerif", Font.PLAIN, 15);

    public static final Font LABEL_FONT =
            new Font("SansSerif", Font.BOLD, 14);

    public static final Font TEXT_FONT =
            new Font("SansSerif", Font.PLAIN, 14);

    public static final Font BUTTON_FONT =
            new Font("SansSerif", Font.BOLD, 15);

    private Theme() {
    }
}