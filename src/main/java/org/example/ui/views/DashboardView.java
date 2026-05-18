package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.*;
import org.example.ui.UI;
import org.example.ui.components.Sidebar;
import org.example.ui.components.Topbar;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard principal do sistema.
 * Utiliza componentes customizados para uma interface moderna.
 */
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

        try {
            setIconImage(
                    new ImageIcon(
                            getClass().getResource("/assets/icon.png")
                    ).getImage()
            );
        } catch (Exception e) {
            System.out.println("Ícone não encontrado.");
        }
    }

    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(usuario.getPerfil());
    }

    private void initComponents() {
        // Criar a Sidebar Customizada
        Sidebar sidebar = new Sidebar();

        // Painel de Conteúdo com CardLayout
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Theme.BACKGROUND);

        // Adicionar as telas ao CardLayout
        contentPanel.add(createPanel("Bem-vindo, " + usuario.getLogin() + "!"), "0");
        contentPanel.add(new ClienteView(clienteService), "1");
        contentPanel.add(new PedidoView(pedidoService, produtoService), "2");
        contentPanel.add(new ProdutoView(produtoService, categoriaService), "3");
        contentPanel.add(new CategoriaView(categoriaService), "4");

        // Adicionando a sua nova tela de Relatórios
        contentPanel.add(new RelatorioView(pedidoService), "5");

        sidebar.addMenuItem("Início", () -> showPanel("0"));
        sidebar.addMenuItem("Clientes", () -> showPanel("1"));
        sidebar.addMenuItem("Pedidos", () -> showPanel("2"));
        sidebar.addMenuItem("Produtos", () -> showPanel("3"));
        sidebar.addMenuItem("Categorias", () -> showPanel("4"));

        // Botão de Relatórios com a trava de segurança
        sidebar.addMenuItem("Relatórios", () -> {
            if (isAdmin()) {
                showPanel("5");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Acesso Negado: Apenas administradores podem acessar os relatórios.",
                        "Controle de Acesso",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        sidebar.addMenuItem("Sair", this::logout);

        // Painel que agrupa a Topbar e o Conteúdo
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(new Topbar("Painel Administrativo"), BorderLayout.NORTH);
        mainContent.add(contentPanel, BorderLayout.CENTER);

        // Root une a Sidebar na esquerda e o MainContent no centro
        root = new JPanel(new BorderLayout());
        root.add(sidebar, BorderLayout.WEST);
        root.add(mainContent, BorderLayout.CENTER);
    }

    private void showPanel(String id) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, id);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair?", "Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView(usuarioService, clienteService, pedidoService, produtoService, categoriaService);
        }
    }

    private JPanel createPanel(String name) {
        return UI.panel(p -> {
                    p.setLayout(new GridBagLayout());
                    p.setBackground(Theme.SURFACE);
                },
                UI.label(name, l -> {
                    l.setFont(Theme.TITLE_FONT);
                    l.setForeground(Theme.PRIMARY);
                })
        );
    }
}