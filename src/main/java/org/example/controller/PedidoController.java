package org.example.controller;

import org.example.model.entity.PedidoEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.service.PedidoService;
import org.example.model.service.ProdutoService;
import org.example.ui.Async;
import org.example.view.PedidoView;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {
    private final PedidoView view;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public PedidoController(
            PedidoView view,
            PedidoService pedidoService,
            ProdutoService produtoService
    ) {
        this.view = view;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    public void carregarProdutos() {
        view.getProdutoCombo().removeAllItems();

        List<ProdutoEntity> produtos =
                produtoService.listarProdutos();

        for (ProdutoEntity produto : produtos) {
            view.getProdutoCombo().addItem(produto);
        }
    }

    public void criarPedido() {
        try {
            Long clienteId = view.requireClienteId();

            Async.compute(
                    () -> pedidoService.criarPedido(clienteId),

                    result -> {
                        JOptionPane.showMessageDialog(
                                view,
                                "Pedido criado!"
                        );
                        carregarPedidos();
                        view.clearFields();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    public void adicionarItem() {
        try {
            Long pedidoId = view.requirePedidoId();

            ProdutoEntity produto =
                    view.getProdutoSelecionado();

            if (produto == null) {
                throw new IllegalArgumentException(
                        "Selecione um produto."
                );
            }

            String qtdTexto =
                    view.getQuantidade();
            if (qtdTexto.isBlank()) {
                throw new IllegalArgumentException(
                        "Digite a quantidade."
                );
            }

            BigDecimal quantidade =
                    new BigDecimal(qtdTexto);

            Async.compute(
                    () -> {
                        pedidoService.adicionarItem(
                                pedidoId,
                                produto.getId(),
                                quantidade
                        );
                        return null;
                    },

                    result -> {
                        JOptionPane.showMessageDialog(
                                view,
                                "Item adicionado!"
                        );
                        carregarPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    public void pagarPedido() {
        try {
            Long pedidoId =
                    view.requirePedidoId();

            Async.compute(
                    () -> {
                        pedidoService.pagarPedido(
                                pedidoId
                        );
                        return null;
                    },

                    result -> {
                        JOptionPane.showMessageDialog(
                                view,
                                "Pago!"
                        );
                        carregarPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    public void cancelarPedido() {
        try {
            Long pedidoId =
                    view.requirePedidoId();

            Async.compute(
                    () -> {
                        pedidoService.cancelarPedido(
                                pedidoId
                        );
                        return null;
                    },

                    result -> {
                        JOptionPane.showMessageDialog(
                                view,
                                "Cancelado!"
                        );
                        carregarPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    public void calcularTotal() {
        try {
            Long pedidoId =
                    view.requirePedidoId();

            Async.compute(
                    () -> pedidoService.calcularTotal(
                            pedidoId
                    ),

                    total -> JOptionPane.showMessageDialog(
                            view,
                            "Total: R$ " + total.intValue()
                    ),
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    // LIMPAR PEDIDOS CONCLUÍDOS
    public void limparPedidosPainel() {
        int confirm =
                JOptionPane.showConfirmDialog(
                        view,
                        """
                        Deseja remover do painel
                        todos os pedidos PAGOS
                        e CANCELADOS?
                        """,
                        "Limpar Painel",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Async.compute(
                () -> {
                    pedidoService
                            .limparPedidosConcluidosDoBanco();
                    return null;
                },

                result -> {
                    JOptionPane.showMessageDialog(
                            view,
                            "Painel atualizado!"
                    );
                    carregarPedidos();
                    view.clearFields();
                },
                this::showError
        );
    }

    public void carregarPedidos() {
        view.getListModel().clear();

        Async.compute(
                () -> {
                    List<PedidoEntity> pedidos =
                            new ArrayList<>();

                    pedidos.addAll(
                            pedidoService
                                    .listarPedidosPorStatus(
                                            "ABERTO"
                                    )
                    );

                    pedidos.addAll(
                            pedidoService
                                    .listarPedidosPorStatus(
                                            "PAGO"
                                    )
                    );

                    pedidos.addAll(
                            pedidoService
                                    .listarPedidosPorStatus(
                                            "CANCELADO"
                                    )
                    );
                    return pedidos;
                },

                pedidos -> {
                    view.setCache(pedidos);

                    for (PedidoEntity pedido : pedidos) {
                        view.getListModel().addElement(
                                "ID: " + pedido.getId()
                                        + " | Cliente: "
                                        + pedido.getCliente().getNome()
                                        + " | Status: "
                                        + pedido.getStatus()
                        );
                    }
                },
                this::showError
        );
    }

    public void showError(Throwable error) {
        String mensagem =
                error.getMessage();

        if (mensagem == null ||
                mensagem.isBlank()) {
            mensagem = "Erro inesperado.";
        }

        JOptionPane.showMessageDialog(
                view,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}