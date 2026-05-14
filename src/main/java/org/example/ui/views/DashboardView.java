package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.UsuarioService;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard principal do sistema.
 * Menu lateral + troca de telas.
 */
public class DashboardView extends JFrame {
    private JPanel root;
    private JPanel menuPanel;
    private JPanel contentPanel;

    private JLabel[] menuItems;
    private JPanel[] panels;

    private JLabel dashboard;
    private JLabel users;
    private JLabel products;
    private JLabel settings;
    private JLabel contact;
    private JLabel calendar;
    private JLabel test;

    private final UsuarioService usuarioService;
    private final UsuarioEntity usuario;

    public DashboardView(UsuarioService usuarioService, UsuarioEntity usuario) {
        this.usuarioService = usuarioService;
        this.usuario = usuario;

        configureFrame();
        initComponents();
        setContentPane(root);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Dashboard - Cantinho do Sabor");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        /*
         * Labels de Menu
         */
        dashboard = createMenuLabel("Dashboard");
        users = createMenuLabel("Users");
        products = createMenuLabel("Products");
        settings = createMenuLabel("Settings");
        contact = createMenuLabel("Contact");
        calendar = createMenuLabel("Calendar");
        test = createMenuLabel("Test");

        menuItems = new JLabel[]{
                dashboard, users, products, settings, contact, calendar, test
        };

        /*
         * Panel (Conteúdo)
         */
        panels = new JPanel[]{
                createPanel("Dashboard"),
                createPanel("Users"),
                createPanel("Products"),
                createPanel("Settings"),
                createPanel("Contact"),
                createPanel("Calendar"),
                createPanel("Test")
        };

        /*
         * Panel Menu
         */
        menuPanel = UI.panel(p -> {
                    p.setLayout(new GridLayout(7, 1));
                    p.setBackground(new Color(46, 49, 49));
                },
                dashboard,
                users,
                products,
                settings,
                contact,
                calendar,
                test
        );

        contentPanel = UI.panel(p -> {
            p.setLayout(new CardLayout());
            p.setBackground(Theme.BACKGROUND);
        });

        for (JPanel panel : panels) {
            contentPanel.add(panel);
        }

        root = UI.panel(p -> {
                    p.setLayout(new BorderLayout());
                },
                menuPanel
        );
        root.add(contentPanel, BorderLayout.CENTER);
        addActions();
    }

    /*
     * Criador de Label (Menu)
     */
    private JLabel createMenuLabel(String text) {
        return UI.label(text, label -> {
            label.setOpaque(true);
            label.setBackground(new Color(46, 49, 49));
            label.setForeground(Color.WHITE);
            label.setFont(Theme.LABEL_FONT);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            label.setBorder(
                    BorderFactory.createMatteBorder(
                            1, 0, 1, 0,
                            new Color(60, 60, 60)
                    )
            );
        });
    }

    /*
     * Criador de Panel
     */
    private JPanel createPanel(String name) {
        return UI.panel(p -> {
                    p.setLayout(new BorderLayout());
                    p.setBackground(Theme.SURFACE);
                },
                UI.label(name, l -> {
                    l.setFont(Theme.TITLE_FONT);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                })
        );
    }

    /*
     * Lógica
     */
    private void addActions() {
        for (int i = 0; i < menuItems.length; i++) {
            int index = i;
            JLabel label = menuItems[i];
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showPanel(index);
                    setActive(label);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    label.setBorder(
                            BorderFactory.createMatteBorder(
                                    1, 0, 1, 0,
                                    Color.YELLOW
                            )
                    );
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    label.setBorder(
                            BorderFactory.createMatteBorder(
                                    1, 0, 1, 0,
                                    new Color(60, 60, 60)
                            )
                    );
                }
            });
        }
        setActive(menuItems[0]);
        showPanel(0);
    }

    /*
     * Troca de Panel
     */
    private void showPanel(int index) {
        for (JPanel panel : panels) {
            panel.setVisible(false);
        }

        panels[index].setVisible(true);
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, String.valueOf(index));
    }

    /*
     * Estilo ativo
     */
    private void setActive(JLabel selected) {
        for (JLabel label : menuItems) {
            label.setBackground(new Color(46, 49, 49));
            label.setForeground(Color.WHITE);
        }
        selected.setBackground(Color.WHITE);
        selected.setForeground(Color.BLUE);
    }
}