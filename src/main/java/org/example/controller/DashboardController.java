package org.example.controller;

import org.example.model.entity.UsuarioEntity;
import org.example.model.service.*;
import org.example.view.LoginView;

import javax.swing.*;
import java.awt.*;

public class DashboardController {
    private final JFrame view;
    private final JPanel contentPanel;
    private final UsuarioService usuarioService;
    private final UsuarioEntity usuario;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public DashboardController(
            JFrame view,
            JPanel contentPanel,

            UsuarioService usuarioService,
            UsuarioEntity usuario,

            ClienteService clienteService,
            PedidoService pedidoService,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {
        this.view = view;
        this.contentPanel = contentPanel;

        this.usuarioService = usuarioService;
        this.usuario = usuario;

        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    // VERIFICA ADMIN
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(
                usuario.getPerfil()
        );
    }

    // TROCAR TELA
    public void showPanel(String id) {
        CardLayout cl =
                (CardLayout)
                        contentPanel.getLayout();
        cl.show(contentPanel, id);
    }

    public void abrirRelatorios() {
        if (isAdmin()) {
            showPanel("5");
            return;
        }

        JOptionPane.showMessageDialog(
                view,
                "Acesso Negado: Apenas administradores podem acessar os relatórios.",
                "Controle de Acesso",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void logout() {
        int confirm =
                JOptionPane.showConfirmDialog(
                        view,
                        "Deseja realmente sair?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.dispose();
        new LoginView(
                usuarioService,
                clienteService,
                pedidoService,
                produtoService,
                categoriaService
        );
    }
}