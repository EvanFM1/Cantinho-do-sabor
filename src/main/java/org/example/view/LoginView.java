package org.example.view;

import org.example.model.entity.UsuarioEntity;
import org.example.model.service.*; // Importando todos os services
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.components.CardPanel; // Importando seu componente
import org.example.ui.components.PrimaryButton; // Usando seu botão roxo
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    private JPanel rootPanel;
    private CardPanel cardPanel;

    private JTextField loginField;
    private JPasswordField passwordField;
    private PrimaryButton loginButton;

    public LoginView(
            UsuarioService usuarioService,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;

        configureFrame();
        initComponents();
        initEvents();

        setContentPane(rootPanel);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Cantinho do Sabor - Login");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(
                new ImageIcon(
                        getClass().getResource("/assets/icon.png")
                ).getImage()
        );
    }

    private void initComponents() {
        // Títulos
        JLabel titleLabel = new JLabel("Cantinho do Sabor");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sistema de Gestão da Sorveteria");
        subtitleLabel.setFont(Theme.TEXT_FONT);
        subtitleLabel.setForeground(Theme.PRIMARY_DARK);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos
        loginField = new JTextField();
        loginField.setBorder(BorderFactory.createTitledBorder("Login"));
        loginField.setMaximumSize(new Dimension(350, 55));

        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Senha"));
        passwordField.setMaximumSize(new Dimension(350, 55));

        // Botão Customizado
        loginButton = new PrimaryButton("Entrar no Sistema");
        loginButton.setMaximumSize(new Dimension(350, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Usando CardPanel
        cardPanel = new CardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(450, 450));

        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(loginField);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(25));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalGlue());

        rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(Theme.BACKGROUND);
        rootPanel.add(cardPanel);
    }

    private void initEvents() {
        loginButton.addActionListener(e -> performLogin());
        Events.keyBinder(rootPanel, key -> key.on("ENTER", this::performLogin));
    }

    private void performLogin() {
        String login = loginField.getText().trim();
        String senha = new String(passwordField.getPassword());

        if (login.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha");
            return;
        }

        loginButton.setText("Autenticando...");
        loginButton.setEnabled(false);

        Async.compute(
                () -> usuarioService.login(login, senha),
                this::onLoginSuccess,
                this::onLoginError
        );
    }

    private void onLoginSuccess(UsuarioEntity usuario) {
        JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getLogin() + "!");
        dispose();

        SwingUtilities.invokeLater(() -> {
            new DashboardView(
                    usuarioService,
                    usuario,
                    clienteService,
                    pedidoService,
                    produtoService,
                    categoriaService
            ).setVisible(true);
        });
    }

    private void onLoginError(Throwable e) {
        loginButton.setText("Entrar no Sistema");
        loginButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Erro ao logar: " + e.getMessage());
    }
}