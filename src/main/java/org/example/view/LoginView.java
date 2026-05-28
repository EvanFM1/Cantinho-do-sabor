package org.example.view;

import org.example.controller.LoginController;
import org.example.model.service.*;
import org.example.ui.Events;
import org.example.ui.components.CardPanel;
import org.example.ui.components.PrimaryButton;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoginView extends JFrame {
    private final LoginController controller;

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

        this.controller =
                new LoginController(
                        this,
                        usuarioService,
                        clienteService,
                        pedidoService,
                        produtoService,
                        categoriaService
                );

        configureFrame();
        initComponents();
        initEvents();

        setContentPane(rootPanel);
        setVisible(true);
    }

    private void configureFrame() {
        setTitle(
                "Cantinho do Sabor - Login"
        );

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        setIconImage(
                new ImageIcon(
                        Objects.requireNonNull(getClass().getResource(
                                "/assets/icon.png"
                        ))
                ).getImage()
        );
    }

    private void initComponents() {
        // TÍTULOS
        JLabel titleLabel =
                new JLabel(
                        "Cantinho do Sabor"
                );

        titleLabel.setFont(
                Theme.TITLE_FONT
        );

        titleLabel.setForeground(
                Theme.PRIMARY
        );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitleLabel =
                new JLabel(
                        "Sistema de Gestão da Sorveteria"
                );

        subtitleLabel.setFont(
                Theme.TEXT_FONT
        );

        subtitleLabel.setForeground(
                Theme.PRIMARY_DARK
        );

        subtitleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        // CAMPOS
        loginField =
                new JTextField();

        loginField.setBorder(
                BorderFactory.createTitledBorder(
                        "Login"
                )
        );

        loginField.setMaximumSize(
                new Dimension(350, 55)
        );

        passwordField =
                new JPasswordField();

        passwordField.setBorder(
                BorderFactory.createTitledBorder(
                        "Senha"
                )
        );

        passwordField.setMaximumSize(
                new Dimension(350, 55)
        );

        // BOTÃO
        loginButton =
                new PrimaryButton(
                        "Entrar no Sistema"
                );

        loginButton.setMaximumSize(
                new Dimension(350, 45)
        );

        loginButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        cardPanel =
                new CardPanel();
        cardPanel.setLayout(
                new BoxLayout(
                        cardPanel,
                        BoxLayout.Y_AXIS
                )
        );

        cardPanel.setPreferredSize(
                new Dimension(450, 450)
        );

        cardPanel.add(
                Box.createVerticalGlue()
        );

        cardPanel.add(titleLabel);
        cardPanel.add(
                Box.createVerticalStrut(10)
        );

        cardPanel.add(subtitleLabel);
        cardPanel.add(
                Box.createVerticalStrut(30)
        );

        cardPanel.add(loginField);
        cardPanel.add(
                Box.createVerticalStrut(15)
        );

        cardPanel.add(passwordField);
        cardPanel.add(
                Box.createVerticalStrut(25)
        );

        cardPanel.add(loginButton);
        cardPanel.add(
                Box.createVerticalGlue()
        );

        rootPanel =
                new JPanel(
                        new GridBagLayout()
                );

        rootPanel.setBackground(
                Theme.BACKGROUND
        );
        rootPanel.add(cardPanel);
    }

    private void initEvents() {
        loginButton.addActionListener(
                e -> controller.performLogin()
        );

        Events.keyBinder(
                rootPanel,
                key -> key.on(
                        "ENTER",
                        controller::performLogin
                )
        );
    }

    public String getLogin() {
        return loginField
                .getText()
                .trim();
    }

    public String getSenha() {
        return new String(
                passwordField.getPassword()
        );
    }

    public void setLoginLoading(
            boolean loading
    ) {

        if (loading) {
            loginButton.setText(
                    "Autenticando..."
            );

            loginButton.setEnabled(false);
            return;
        }

        loginButton.setText(
                "Entrar no Sistema"
        );
        loginButton.setEnabled(true);
    }
}