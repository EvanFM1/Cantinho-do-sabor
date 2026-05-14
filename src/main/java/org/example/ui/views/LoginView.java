package org.example.ui.views;

import org.example.entity.UsuarioEntity;
import org.example.service.UsuarioService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela de login principal do sistema.
 */
public class LoginView extends JFrame {
    private final UsuarioService usuarioService;
    private JPanel rootPanel;
    private JPanel cardPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    /**
     * Construtor principal.
     */
    public LoginView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        configureFrame();
        initComponents();
        initEvents();
        setContentPane(rootPanel);

        /*
         * Swing VAI iniciar o botão com o estilo certo
         */
        SwingUtilities.updateComponentTreeUI(this);
        setVisible(true);
    }

    /**
     * Configurações da janela.
     */
    private void configureFrame() {
        setTitle("Cantinho do Sabor");
        setSize(1200, 700);
        setMinimumSize(
                new Dimension(900, 600)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Inicializa componentes.
     */
    private void initComponents() {
        /*
         * Título
         */
        titleLabel = UI.label(
                "Cantinho do Sabor",
                label -> {
                    label.setFont(Theme.TITLE_FONT);
                    label.setForeground(Theme.PRIMARY);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
        );

        subtitleLabel = UI.label(
                "Sistema de Gestão da Sorveteria",
                label -> {
                    label.setFont(Theme.TEXT_FONT);
                    label.setForeground(Theme.PRIMARY_DARK);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
        );

        /*
         * Labels
         */
        loginLabel = UI.label(
                "Login",
                label -> {
                    label.setFont(Theme.LABEL_FONT);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
        );

        passwordLabel = UI.label(
                "Senha",
                label -> {
                    label.setFont(Theme.LABEL_FONT);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
        );

        /*
         * Campos
         */
        loginField = UI.textField(field -> {
            field.setFont(Theme.TEXT_FONT);
            field.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 42)
            );

            field.setAlignmentX(Component.CENTER_ALIGNMENT);
        });

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.TEXT_FONT);
        passwordField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = UI.button(
                "Entrar",
                button -> {
                    button.setFont(Theme.BUTTON_FONT);
                    button.setBackground(Theme.PRIMARY);

                    /*
                     * Texto branco garantido
                     */
                    button.setForeground(new Color(255, 255, 255));
                    button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
                    button.setOpaque(true);
                    button.setContentAreaFilled(true);
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setFocusable(false);
                    button.setCursor(
                            new Cursor(Cursor.HAND_CURSOR)
                    );

                    button.setAlignmentX(Component.CENTER_ALIGNMENT);

                    button.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 48)
                    );

                    /*
                     * Começa habilitado pra não bugar o look and feel
                     */
                    button.setEnabled(true);
                }
        );

        cardPanel = UI.panel(
                panel -> {
                    panel.setLayout(
                            new BoxLayout(panel, BoxLayout.Y_AXIS)
                    );

                    panel.setBackground(Theme.SURFACE);

                    panel.setBorder(
                            new EmptyBorder(40, 40, 40, 40)
                    );

                    panel.setPreferredSize(
                            new Dimension(420, 430)
                    );
                },
                titleLabel,

                Box.createVerticalStrut(10),
                subtitleLabel,

                Box.createVerticalStrut(40),
                loginLabel,

                Box.createVerticalStrut(8),
                loginField,

                Box.createVerticalStrut(20),
                passwordLabel,

                Box.createVerticalStrut(8),
                passwordField,

                Box.createVerticalStrut(35),
                loginButton
        );

        rootPanel = UI.panel(
                panel -> {
                    panel.setLayout(
                            new GridBagLayout()
                    );

                    panel.setBackground(
                            Theme.BACKGROUND
                    );
                },
                cardPanel
        );
        validateFields();
    }

    /**
     * Eventos.
     */
    private void initEvents() {
        /*
         * Hover no botão
         */
        Events.mouse(loginButton, mouse -> {
            mouse.onEntered(event -> {
                loginButton.setBackground(
                        Theme.PRIMARY_DARK
                );
            });

            mouse.onExited(event -> {
                loginButton.setBackground(
                        Theme.PRIMARY
                );
            });
        });

        /*
         * Enter
         */
        Events.keyBinder(rootPanel, key -> {
            key.on("ENTER", this::performLogin);
        });

        /*
         * Validação
         */
        Events.text(
                loginField,
                text -> validateFields()
        );

        Events.text(
                passwordField,
                text -> validateFields()
        );

        /*
         * Clique, sim
         */
        loginButton.addActionListener(
                event -> performLogin()
        );
    }

    /**
     * Validação dos campos.
     */
    private void validateFields() {
        boolean loading =
                Boolean.TRUE.equals(
                        loginButton.getClientProperty("loading")
                );

        if (loading) {
            return;
        }

        boolean valid =
                !loginField.getText().isBlank()
                        &&
                        passwordField.getPassword().length > 0;
        loginButton.setBackground(
                valid
                        ? Theme.PRIMARY
                        : Theme.PRIMARY_DARK
        );
    }

    /**
     * Executa login.
     */
    private void performLogin() {
        /*
         * Impede duplo clique
         */
        boolean loading =
                Boolean.TRUE.equals(
                        loginButton.getClientProperty("loading")
                );

        if (loading) {
            return;
        }
        String login =
                loginField.getText().trim();
        String senha =
                new String(
                        passwordField.getPassword()
                );

        /*
         * Validação
         */
        if (login.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha login e senha.",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        /*
         * Loading
         */
        loginButton.putClientProperty("loading", true);
        loginButton.setText("Entrando...");
        loginButton.setBackground(
                Theme.PRIMARY_DARK
        );

        /*
         * Login assíncrono
         */
        Async.compute(
                () -> usuarioService.login(login, senha),
                this::onLoginSuccess,
                this::onLoginError
        );
    }

    /**
     * Login OK.
     */
    private void onLoginSuccess(
            UsuarioEntity usuario
    ) {
        JOptionPane.showMessageDialog(
                this,
                "Bem-vindo, "
                        + usuario.getLogin()
                        + " ("
                        + usuario.getPerfil()
                        + ")",
                "Login realizado",
                JOptionPane.INFORMATION_MESSAGE
        );
        dispose();

        /*
         * Abre meu dashboard querido
         */
        SwingUtilities.invokeLater(() -> {
            new DashboardView(usuarioService, usuario).setVisible(true);
        });
    }

    private void onLoginError(
            Throwable throwable
    ) {
        /*
         * Remove loading
         */
        loginButton.putClientProperty(
                "loading",
                false
        );
        loginButton.setText("Entrar");
        validateFields();
        JOptionPane.showMessageDialog(
                this,
                throwable.getMessage(),
                "Erro no login",
                JOptionPane.ERROR_MESSAGE
        );
    }
}