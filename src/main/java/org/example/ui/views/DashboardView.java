package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.ClienteService;
import org.example.service.PedidoService;
import org.example.service.ProdutoService;
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
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public DashboardView(
            UsuarioService usuarioService,
            UsuarioEntity usuario,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService
    ) {
        this.usuarioService = usuarioService;
        this.usuario = usuario;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;

        configureFrame();
        initComponents();
        setContentPane(root);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Menu - Cantinho do Sabor");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /*
    É admin?
     */
    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(usuario.getPerfil());
    }

    private void initComponents() {
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

        panels = new JPanel[]{
                createPanel("Dashboard"),
                new ClienteView(clienteService),
                new PedidoView(pedidoService),
                new ProdutoView(produtoService),
                createPanel("Settings"),
                createPanel("Contact"),
                createPanel("Calendar"),
                createPanel("Test")
        };

        /*
        Bloqueio Admin (campo funcionários)
         */
        if (!isAdmin()) {
            users.setVisible(false); // ou setEnabled(false)
        }

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
                    label.setBorder(BorderFactory.createMatteBorder(
                            1, 0, 1, 0, Color.YELLOW));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    label.setBorder(BorderFactory.createMatteBorder(
                            1, 0, 1, 0, new Color(60, 60, 60)));
                }
            });
        }
        setActive(menuItems[0]);
        showPanel(0);
    }

    private void showPanel(int index) {
        for (JPanel panel : panels) {
            panel.setVisible(false);
        }
        panels[index].setVisible(true);
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, String.valueOf(index));
    }

    /*
    Estilo ativo
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