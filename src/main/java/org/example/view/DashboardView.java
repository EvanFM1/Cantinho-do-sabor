package org.example.view;

import org.example.controller.DashboardController;
import org.example.model.entity.UsuarioEntity;
import org.example.model.service.*;
import org.example.ui.UI;
import org.example.ui.components.Sidebar;
import org.example.ui.components.Topbar;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DashboardView extends JFrame {
    private JPanel root;
    private JPanel contentPanel;

    private final DashboardController controller;

    public DashboardView(
            UsuarioService usuarioService,
            UsuarioEntity usuario,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {

        configureFrame();
        initComponents(
                clienteService,
                pedidoService,
                produtoService,
                categoriaService,
                usuario
        );

        this.controller =
                new DashboardController(
                        this,
                        contentPanel,
                        usuarioService,
                        usuario,
                        clienteService,
                        pedidoService,
                        produtoService,
                        categoriaService
                );

        configureSidebar();
        setContentPane(root);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle(
                "Cantinho do Sabor - Sistema de Gestão"
        );

        setSize(1280, 720);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        try {
            setIconImage(
                    new ImageIcon(
                            Objects.requireNonNull(getClass().getResource(
                                    "/assets/icon.png"
                            ))
                    ).getImage()
            );
        } catch (Exception e) {
            System.out.println(
                    "Ícone não encontrado."
            );
        }
    }

    private void initComponents(
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService,
            UsuarioEntity usuario
    ) {

        contentPanel =
                new JPanel(new CardLayout());

        contentPanel.setBackground(
                Theme.BACKGROUND
        );

        // TELAS
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
                new PedidoView(
                        pedidoService,
                        produtoService
                ),
                "2"
        );

        contentPanel.add(
                new ProdutoView(
                        produtoService,
                        categoriaService
                ),
                "3"
        );

        contentPanel.add(
                new CategoriaView(
                        categoriaService
                ),
                "4"
        );

        contentPanel.add(
                new RelatorioView(
                        pedidoService
                ),
                "5"
        );

        JPanel mainContent =
                new JPanel(new BorderLayout());

        mainContent.add(
                new Topbar(
                        "Painel Administrativo"
                ),
                BorderLayout.NORTH
        );

        mainContent.add(
                contentPanel,
                BorderLayout.CENTER
        );

        root =
                new JPanel(new BorderLayout());
        root.add(
                createSidebar(),
                BorderLayout.WEST
        );

        root.add(
                mainContent,
                BorderLayout.CENTER
        );
    }

    private Sidebar createSidebar() {
        Sidebar sidebar =
                new Sidebar();

        sidebar.addMenuItem(
                "Início",
                () -> controller.showPanel("0")
        );

        sidebar.addMenuItem(
                "Clientes",
                () -> controller.showPanel("1")
        );

        sidebar.addMenuItem(
                "Pedidos",
                () -> controller.showPanel("2")
        );

        sidebar.addMenuItem(
                "Produtos",
                () -> controller.showPanel("3")
        );

        sidebar.addMenuItem(
                "Categorias",
                () -> controller.showPanel("4")
        );

        sidebar.addMenuItem(
                "Relatórios",
                () -> controller.abrirRelatorios()
        );

        sidebar.addMenuItem(
                "Sair",
                () -> controller.logout()
        );
        return sidebar;
    }

    private void configureSidebar() {
        controller.showPanel("0");
    }

    private JPanel createPanel(String name) {
        return UI.panel(p -> {
                    p.setLayout(
                            new GridBagLayout()
                    );

                    p.setBackground(
                            Theme.SURFACE
                    );

                },

                UI.label(name, l -> {
                    l.setFont(
                            Theme.TITLE_FONT
                    );

                    l.setForeground(
                            Theme.PRIMARY
                    );
                })
        );
    }
}