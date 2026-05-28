package org.example.controller;

import org.example.model.entity.CategoriaEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.service.CategoriaService;
import org.example.model.service.ProdutoService;
import org.example.ui.Async;
import org.example.view.ProdutoView;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoController {
    private final ProdutoView view;
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(
            ProdutoView view,
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {
        this.view = view;
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    public void carregarCategorias() {
        view.getCategoriaCombo().removeAllItems();

        List<CategoriaEntity> categorias =
                categoriaService.listarCategorias();

        for (CategoriaEntity categoria : categorias) {
            view.getCategoriaCombo().addItem(
                    categoria
            );
        }
    }

    public void criarProduto() {
        String nome =
                view.getNome();

        String descricao =
                view.getDescricao();

        String preco =
                view.getPreco();

        String estoque =
                view.getEstoque();

        if (nome.isBlank()
                || preco.isBlank()
                || estoque.isBlank()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Preencha os campos obrigatórios!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        CategoriaEntity categoria =
                view.getCategoriaSelecionada();

        if (categoria == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Selecione uma categoria!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Async.compute(
                () -> {
                    ProdutoEntity produto =
                            new ProdutoEntity();

                    produto.setNome(nome);
                    produto.setDescricao(
                            descricao
                    );

                    produto.setPreco(
                            new BigDecimal(preco)
                    );

                    produto.setEstoque(
                            new BigDecimal(estoque)
                    );

                    produtoService.criarProduto(
                            produto,
                            categoria.getId()
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            view,
                            "Produto criado com sucesso!"
                    );
                    view.clearFields();
                    carregarProdutos();
                },
                this::showError
        );
    }

    public void deletarProduto() {
        Long id;

        try {
            if (!view.getProdutoId().isBlank()) {
                id = Long.parseLong(
                        view.getProdutoId()
                );

            } else {
                ProdutoEntity produto =
                        view.getProdutoSelecionado();

                if (produto == null) {
                    JOptionPane.showMessageDialog(
                            view,
                            "Selecione um produto!"
                    );
                    return;
                }
                id = produto.getId();
            }
        } catch (Exception e) {
            showError(e);
            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        view,
                        "Deseja deletar o produto?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Long finalId = id;
        Async.compute(
                () -> {
                    produtoService.deletarProduto(
                            finalId
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            view,
                            "Produto deletado!"
                    );
                    view.clearFields();
                    carregarProdutos();
                },
                this::showError
        );
    }

    public void adicionarEstoque() {
        alterarEstoque(true);
    }

    public void removerEstoque() {
        alterarEstoque(false);
    }

    private void alterarEstoque(
            boolean adicionar
    ) {

        try {
            String idTexto =
                    view.getProdutoId();

            String qtdTexto =
                    view.getEstoque();

            if (idTexto.isBlank()) {
                JOptionPane.showMessageDialog(
                        view,
                        """
                        Selecione um produto
                        na lista antes de alterar
                        o estoque!
                        """,
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (qtdTexto.isBlank()) {
                JOptionPane.showMessageDialog(
                        view,
                        "Informe a quantidade!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            Long id;
            BigDecimal quantidade;

            try {
                id = Long.parseLong(
                        idTexto
                );
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        view,
                        "Produto inválido!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                quantidade =
                        new BigDecimal(
                                qtdTexto
                        );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        view,
                        "Quantidade inválida!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Async.compute(
                    () -> {
                        if (adicionar) {
                            produtoService
                                    .adicionarEstoque(
                                            id,
                                            quantidade
                                    );

                        } else {
                            produtoService
                                    .removerEstoque(
                                            id,
                                            quantidade
                                    );
                        }
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                view,
                                adicionar
                                        ? "Estoque adicionado!"
                                        : "Estoque removido!"
                        );
                        carregarProdutos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    public void carregarProdutos() {
        view.getListModel().clear();

        Async.compute(
                produtoService::listarProdutos,

                produtos -> {
                    view.setCache(produtos);

                    for (ProdutoEntity produto : produtos) {
                        view.getListModel().addElement(
                                "• ID: "
                                        + produto.getId()
                                        + " | "
                                        + produto.getNome()
                                        + " | R$ "
                                        + produto.getPreco()
                                        + " | Estoque: "
                                        + produto.getEstoque().intValue()
                                        + " | Categoria: "
                                        + produto.getCategoria()
                                        .getNome()
                        );
                    }
                },
                this::showError
        );
    }

    public void showError(Throwable e) {
        JOptionPane.showMessageDialog(
                view,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}