package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.ClienteService;
import org.example.service.PedidoService;
import org.example.service.ProdutoService;
import org.example.service.UsuarioService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    private JPanel rootPanel;
    private JPanel cardPanel;

    private JLabel titleLabel;
    private JLabel subtitleLabel;

    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView(
            UsuarioService usuarioService,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService
    ) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;

        configureFrame();
        initComponents();
        initEvents();

        setContentPane(rootPanel);
        SwingUtilities.updateComponentTreeUI(this);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle("Cantinho do Sabor");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initComponents() {
        titleLabel = UI.label("Cantinho do Sabor", l -> {
            l.setFont(Theme.TITLE_FONT);
            l.setForeground(Theme.PRIMARY);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        });

        subtitleLabel = UI.label("Sistema de Gestão da Sorveteria", l -> {
            l.setFont(Theme.TEXT_FONT);
            l.setForeground(Theme.PRIMARY_DARK);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        });

        loginField = UI.textField(f -> {
            f.setFont(Theme.TEXT_FONT);
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        });

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.TEXT_FONT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        loginButton = UI.button("Entrar", b -> {
            b.setFont(Theme.BUTTON_FONT);
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        });

        cardPanel = UI.panel(p -> {
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    p.setBackground(Theme.SURFACE);
                    p.setBorder(new EmptyBorder(40, 40, 40, 40));
                },
                titleLabel,
                Box.createVerticalStrut(10),
                subtitleLabel,
                Box.createVerticalStrut(30),
                loginField,
                Box.createVerticalStrut(10),
                passwordField,
                Box.createVerticalStrut(20),
                loginButton
        );

        rootPanel = UI.panel(p -> {
            p.setLayout(new GridBagLayout());
            p.setBackground(Theme.BACKGROUND);
        }, cardPanel);
    }

    private void initEvents() {
        loginButton.addActionListener(e -> performLogin());

        Events.keyBinder(rootPanel, key -> {
            key.on("ENTER", this::performLogin);
        });
    }

    private void performLogin() {
        String login = loginField.getText().trim();
        String senha = new String(passwordField.getPassword());

        if (login.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha");
            return;
        }

        loginButton.setText("Entrando...");
        loginButton.setEnabled(false);
        Async.compute(
                () -> usuarioService.login(login, senha),
                this::onLoginSuccess,
                this::onLoginError
        );
    }

    private void onLoginSuccess(UsuarioEntity usuario) {
        JOptionPane.showMessageDialog(
                this,
                "Bem-vindo " + usuario.getLogin()
        );
        dispose();
        SwingUtilities.invokeLater(() -> {
            new DashboardView(
                    usuarioService,
                    usuario,
                    clienteService,
                    pedidoService,
                    produtoService
            ).setVisible(true);
        });
    }

    private void onLoginError(Throwable e) {
        loginButton.setText("Entrar");
        loginButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}