package org.example.controller;

import org.example.model.entity.UsuarioEntity;
import org.example.model.service.*;
import org.example.ui.Async;
import org.example.view.DashboardView;
import org.example.view.LoginView;

import javax.swing.*;

public class LoginController {
    private final LoginView view;

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public LoginController(
            LoginView view,

            UsuarioService usuarioService,
            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {

        this.view = view;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    // LOGIN
    public void performLogin() {
        String login =
                view.getLogin();

        String senha =
                view.getSenha();

        if (login.isBlank() ||
                senha.isBlank()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Preencha login e senha"
            );
            return;
        }

        view.setLoginLoading(true);
        Async.compute(
                () -> usuarioService.login(
                        login,
                        senha
                ),

                this::onLoginSuccess,
                this::onLoginError
        );
    }

    private void onLoginSuccess(
            UsuarioEntity usuario
    ) {

        JOptionPane.showMessageDialog(
                view,
                "Bem-vindo, "
                        + usuario.getLogin()
                        + "!"
        );

        view.dispose();
        SwingUtilities.invokeLater(() -> new DashboardView(
                usuarioService,
                usuario,

                clienteService,
                pedidoService,
                produtoService,
                categoriaService
        ).setVisible(true));
    }

    private void onLoginError(
            Throwable e
    ) {

        view.setLoginLoading(false);

        JOptionPane.showMessageDialog(
                view,
                "Erro ao logar: "
                        + e.getMessage()
        );
    }
}