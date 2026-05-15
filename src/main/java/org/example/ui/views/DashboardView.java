package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.*;
import org.example.ui.UI;
import org.example.ui.bindings.key.KeyBinder;
import org.example.ui.components.Sidebar;
import org.example.ui.components.Topbar;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private JPanel root;
    private JPanel contentPanel;
    private final UsuarioService usuarioService;
    private final UsuarioEntity usuario;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public DashboardView(
            UsuarioService usuarioService,
            UsuarioEntity usuario,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {
        this.usuarioService = usuarioService;
        this.usuario = usuario;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;

        configureFrame();
        initComponents();
        setContentPane(root);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Cantinho do Sabor - Sistema de Gestão");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /*
        Ícone
         */
        setIconImage(
                new ImageIcon(
                        getClass().getResource("/assets/icon.png")
                ).getImage()
        );
    }

    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(usuario.getPerfil());
    }

    private void initComponents() {
        /*
        Sidebar
         */
        Sidebar sidebar = new Sidebar();

        /*
        Content
         */
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Theme.BACKGROUND);

        /*
        Views
         */
        contentPanel.add(
                createPanel(
                        "Bem-vindo, "
                                + usuario.getLogin()
                                + "!"
                ),
                "0"
        );

        contentPanel.add(
                new ClienteView(clienteService),
                "1"
        );

        contentPanel.add(
                new PedidoView(pedidoService),
                "2"
        );

        contentPanel.add(
                new ProdutoView(
                        produtoService,
                        categoriaService
                ),
                "3"
        );

        /*
        NOVA VIEW
         */
        contentPanel.add(
                new CategoriaView(categoriaService),
                "4"
        );

        contentPanel.add(
                createPanel("Configurações do Sistema"),
                "5"
        );

        /*
        Menus
         */
        sidebar.addMenuItem(
                "Início",
                () -> showPanel("0")
        );

        if (isAdmin()) {
            sidebar.addMenuItem(
                    "Clientes",
                    () -> showPanel("1")
            );
        }

        sidebar.addMenuItem(
                "Pedidos",
                () -> showPanel("2")
        );

        sidebar.addMenuItem(
                "Produtos",
                () -> showPanel("3")
        );

        sidebar.addMenuItem(
                "Categorias",
                () -> showPanel("4")
        );

        sidebar.addMenuItem(
                "Ajustes",
                () -> showPanel("5")
        );

        sidebar.addMenuItem(
                "Sair",
                this::logout
        );

        /*
        Main Content
         */
        JPanel mainContent = UI.panel(
                p -> {
                    p.setLayout(new BorderLayout());
                    p.setBackground(Theme.BACKGROUND);
                }
        );

        mainContent.add(
                new Topbar("Painel Administrativo"),
                BorderLayout.NORTH
        );

        mainContent.add(
                contentPanel,
                BorderLayout.CENTER
        );

        /*
        Root
         */
        root = UI.panel(
                p -> {
                    p.setLayout(new BorderLayout());
                    p.setBackground(Theme.BACKGROUND);
                }
        );
        root.add(sidebar, BorderLayout.WEST);
        root.add(mainContent, BorderLayout.CENTER);

        // ESC fecha aplicação
        new KeyBinder(root)
                .on("ESCAPE", () -> {
                    int confirm =
                            JOptionPane.showConfirmDialog(
                                    this,
                                    "Deseja sair?",
                                    "Sair",
                                    JOptionPane.YES_NO_OPTION
                            );
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                });
    }

    private void showPanel(String id) {
        CardLayout cl =
                (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, id);
    }

    private void logout() {
        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente sair?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView(
                    usuarioService,
                    clienteService,
                    pedidoService,
                    produtoService,
                    categoriaService
            );
        }
    }

    private JPanel createPanel(String name) {
        return UI.panel(
                p -> {
                    p.setLayout(new GridBagLayout());
                    p.setBackground(Theme.SURFACE);
                },

                UI.label(name, label -> {
                    label.setFont(Theme.TITLE_FONT);
                    label.setForeground(Theme.PRIMARY);
                })
        );
    }
}