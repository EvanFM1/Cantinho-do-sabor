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

        /*
        LISTA E RENDERIZADOR DE CORES
         */
        listModel = new DefaultListModel<>();
        listaPedidos = new JList<>(listModel);
        listaPedidos.setFont(Theme.TEXT_FONT);

        // Define como a lista vai pintar as linhas baseado no status text
        listaPedidos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null) {
                    String text = value.toString();

                    if (text.contains("Status: PAGO")) {
                        c.setBackground(new Color(200, 230, 201)); // Verde claro
                        c.setForeground(new Color(30, 90, 30));     // Texto verde escuro
                    } else if (text.contains("Status: CANCELADO")) {
                        c.setBackground(new Color(255, 205, 210)); // Vermelho claro
                        c.setForeground(new Color(150, 30, 30));    // Texto vermelho escuro
                    } else {
                        // Comportamento padrão para pedidos em ABERTO
                        if (isSelected) {
                            c.setBackground(list.getSelectionBackground());
                            c.setForeground(list.getSelectionForeground());
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }
                }

                // Reaplica a cor de seleção do sistema caso o usuário clique na linha (opcional para feedback visual)
                if (isSelected) {
                    setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 2));
                } else {
                    setBorder(new EmptyBorder(8, 10, 8, 10));
                }

                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        /*
        CAMPOS
         */
        clienteIdField = createField("Cliente ID");
        pedidoIdField = createField("Pedido ID (selecionado)");
        quantidadeField = createField("Quantidade");

        /*
        PRODUTO COMBO
         */
        produtoCombo = new JComboBox<>();
        produtoCombo.setBorder(BorderFactory.createTitledBorder("Produto"));

        /*
        BOTÕES
         */
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

        /*
        FORM
         */
        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(14, 1, 5, 5));
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
                listarButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
        EVENTS
         */
        criarButton.addActionListener(e -> criarPedido());
        pagarButton.addActionListener(e -> pagarPedido());
        cancelarButton.addActionListener(e -> cancelarPedido());
        totalButton.addActionListener(e -> calcularTotal());
        listarButton.addActionListener(e -> {
            loadPedidos();
            loadProdutos();
        });
        adicionarItemButton.addActionListener(e -> adicionarItem());

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

    /*
    CARREGAR PRODUTOS
     */
    private void loadProdutos() {
        System.out.println("CARREGANDO PRODUTOS...");
        System.out.println(produtoService.listarProdutos().size());
        produtoCombo.removeAllItems();

        List<ProdutoEntity> produtos = produtoService.listarProdutos();
        for (ProdutoEntity p : produtos) {
            produtoCombo.addItem(p);
        }
    }

    /*
    CRIAR PEDIDO
     */
    private void criarPedido() {
        try {
            Long clienteId = Long.parseLong(clienteIdField.getText().trim());

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

    /*
    ADICIONAR ITEM
     */
    private void adicionarItem() {
        try {
            Long pedidoId = Long.parseLong(pedidoIdField.getText().trim());
            ProdutoEntity produto = (ProdutoEntity) produtoCombo.getSelectedItem();
            BigDecimal qtd = new BigDecimal(quantidadeField.getText().trim());

            if (produto == null) {
                throw new RuntimeException("Selecione um produto!");
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

    /*
    PAGAR
     */
    private void pagarPedido() {
        try {
            Long id = Long.parseLong(pedidoIdField.getText().trim());

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

    /*
    CANCELAR
     */
    private void cancelarPedido() {
        try {
            Long id = Long.parseLong(pedidoIdField.getText().trim());

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

    /*
    TOTAL
     */
    private void calcularTotal() {
        try {
            Long id = Long.parseLong(pedidoIdField.getText().trim());

            Async.compute(
                    () -> pedidoService.calcularTotal(id),
                    total -> JOptionPane.showMessageDialog(this, "Total: R$ " + total.intValue()),
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    LISTAR (AGORA JUNTA ABERTO, PAGO E CANCELADO)
     */
    private void loadPedidos() {
        listModel.clear();
        Async.compute(
                () -> {
                    // Puxa as três listas separadas do banco e junta tudo em uma lista unificada
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

    /*
    SELEÇÃO
     */
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
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}