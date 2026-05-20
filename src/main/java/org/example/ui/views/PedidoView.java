package org.example.ui.views;

import org.example.entity.PedidoEntity;
import org.example.entity.ProdutoEntity;
import org.example.service.PedidoService;
import org.example.service.ProdutoService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoView extends JPanel {
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    private DefaultListModel<String> listModel;
    private JList<String> listaPedidos;

    private JTextField clienteIdField;
    private JTextField pedidoIdField;
    private JTextField quantidadeField;

    private JComboBox<ProdutoEntity> produtoCombo;

    private JButton criarButton;
    private JButton cancelarButton;
    private JButton pagarButton;
    private JButton listarButton;
    private JButton totalButton;
    private JButton adicionarItemButton;
    private JButton limparConcluidosButton;

    private List<PedidoEntity> cache;

    public PedidoView(PedidoService pedidoService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;

        initComponents();
        loadPedidos();
        loadProdutos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        listModel = new DefaultListModel<>();
        listaPedidos = new JList<>(listModel);
        listaPedidos.setFont(Theme.TEXT_FONT);

        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        clienteIdField = createField("Cliente ID");
        pedidoIdField = createField("Pedido ID (selecionado)");
        quantidadeField = createField("Quantidade");

        produtoCombo = new JComboBox<>();
        produtoCombo.setBorder(BorderFactory.createTitledBorder("Produto"));

        criarButton = UI.button("Criar Pedido", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        pagarButton = UI.button("Pagar", b -> {
            b.setBackground(new Color(0, 120, 0));
            b.setForeground(Color.WHITE);
        });

        cancelarButton = UI.button("Cancelar", b -> {
            b.setBackground(new Color(200, 60, 60));
            b.setForeground(Color.WHITE);
        });

        adicionarItemButton = UI.button("Adicionar Item", b -> {
            b.setBackground(new Color(80, 80, 200));
            b.setForeground(Color.WHITE);
        });

        listarButton = UI.button("Atualizar", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        totalButton = UI.button("Total", b -> {
            b.setBackground(new Color(70, 70, 70));
            b.setForeground(Color.WHITE);
        });

        limparConcluidosButton = UI.button("Limpar Concluídos", b -> {
            b.setBackground(new Color(40, 40, 40));
            b.setForeground(Color.WHITE);
        });

        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(15, 1, 5, 5));
                    p.setBackground(Theme.SURFACE);
                    p.setBorder(new EmptyBorder(20, 20, 20, 20));
                    p.setPreferredSize(new Dimension(320, 0));
                },
                clienteIdField,
                pedidoIdField,
                produtoCombo,
                quantidadeField,
                criarButton,
                adicionarItemButton,
                pagarButton,
                cancelarButton,
                totalButton,
                listarButton,
                limparConcluidosButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        criarButton.addActionListener(e -> criarPedido());
        pagarButton.addActionListener(e -> pagarPedido());
        cancelarButton.addActionListener(e -> cancelarPedido());
        totalButton.addActionListener(e -> calcularTotal());
        listarButton.addActionListener(e -> {
            loadPedidos();
            loadProdutos();
        });
        adicionarItemButton.addActionListener(e -> adicionarItem());
        limparConcluidosButton.addActionListener(e -> limparPedidosPainel());

        listaPedidos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setFont(Theme.TEXT_FONT);
        return field;
    }

    private Long requirePedidoId() {
        String text = pedidoIdField.getText().trim();

        if (text.isBlank()) {
            throw new IllegalArgumentException("Selecione um pedido na lista.");
        }

        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Pedido inválido selecionado.");
        }
    }

    private Long requireClienteId() {
        String text = clienteIdField.getText().trim();

        if (text.isBlank()) {
            throw new IllegalArgumentException("Informe o ID do cliente.");
        }

        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID do cliente inválido.");
        }
    }

    private void limparPedidosPainel() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja remover do painel todos os pedidos PAGOS e CANCELADOS?",
                "Limpar Painel", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Async.compute(
                    () -> {
                        pedidoService.limparPedidosConcluidosDoBanco();
                        return null;
                    },
                    r -> {
                        JOptionPane.showMessageDialog(this, "Painel atualizado!");
                        loadPedidos();
                        clearFields();
                    },
                    this::showError
            );
        }
    }

    private void loadProdutos() {
        produtoCombo.removeAllItems();

        List<ProdutoEntity> produtos = produtoService.listarProdutos();
        for (ProdutoEntity p : produtos) {
            produtoCombo.addItem(p);
        }
    }

    private void criarPedido() {
        try {
            Long clienteId = requireClienteId();

            Async.compute(
                    () -> pedidoService.criarPedido(clienteId),
                    r -> {
                        JOptionPane.showMessageDialog(this, "Pedido criado!");
                        loadPedidos();
                        clearFields();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void adicionarItem() {
        try {
            Long pedidoId = requirePedidoId();
            ProdutoEntity produto = (ProdutoEntity) produtoCombo.getSelectedItem();
            BigDecimal qtd = new BigDecimal(quantidadeField.getText().trim());

            if (produto == null) {
                throw new IllegalArgumentException("Selecione um produto.");
            }

            Async.compute(
                    () -> {
                        pedidoService.adicionarItem(
                                pedidoId,
                                produto.getId(),
                                qtd
                        );
                        return null;
                    },
                    r -> {
                        JOptionPane.showMessageDialog(this, "Item adicionado!");
                        loadPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void pagarPedido() {
        try {
            Long id = requirePedidoId();

            Async.compute(
                    () -> {
                        pedidoService.pagarPedido(id);
                        return null;
                    },
                    r -> {
                        JOptionPane.showMessageDialog(this, "Pago!");
                        loadPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void cancelarPedido() {
        try {
            Long id = requirePedidoId();

            Async.compute(
                    () -> {
                        pedidoService.cancelarPedido(id);
                        return null;
                    },
                    r -> {
                        JOptionPane.showMessageDialog(this, "Cancelado!");
                        loadPedidos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void calcularTotal() {
        try {
            Long id = requirePedidoId();

            Async.compute(
                    () -> pedidoService.calcularTotal(id),
                    total -> JOptionPane.showMessageDialog(this, "Total: R$ " + total.intValue()),
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadPedidos() {
        listModel.clear();
        Async.compute(
                () -> {
                    List<PedidoEntity> tudo = new ArrayList<>();
                    tudo.addAll(pedidoService.listarPedidosPorStatus("ABERTO"));
                    tudo.addAll(pedidoService.listarPedidosPorStatus("PAGO"));
                    tudo.addAll(pedidoService.listarPedidosPorStatus("CANCELADO"));
                    return tudo;
                },
                pedidos -> {
                    cache = pedidos;

                    for (PedidoEntity p : pedidos) {
                        listModel.addElement(
                                "ID: " + p.getId()
                                        + " | Cliente: " + p.getCliente().getNome()
                                        + " | Status: " + p.getStatus()
                        );
                    }
                },
                this::showError
        );
    }

    private void preencherCampos() {
        int index = listaPedidos.getSelectedIndex();

        if (index < 0 || cache == null) return;

        PedidoEntity p = cache.get(index);
        pedidoIdField.setText(String.valueOf(p.getId()));
        clienteIdField.setText(String.valueOf(p.getCliente().getId()));
    }

    private void clearFields() {
        clienteIdField.setText("");
        pedidoIdField.setText("");
        quantidadeField.setText("");
        listaPedidos.clearSelection();
    }

    private void showError(Throwable e) {
        String msg = e.getMessage();

        if (msg == null || msg.isBlank()) {
            msg = "Erro inesperado.";
        }

        JOptionPane.showMessageDialog(
                this,
                msg,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}