package org.example.view;

import org.example.controller.PedidoController;
import org.example.model.entity.PedidoEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.service.PedidoService;
import org.example.model.service.ProdutoService;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PedidoView extends JPanel {
    private final PedidoController controller;

    private DefaultListModel<String> listModel;
    private JList<String> listaPedidos;

    private JTextField clienteIdField;
    private JTextField pedidoIdField;
    private JTextField quantidadeField;

    private JComboBox<ProdutoEntity> produtoCombo;
    private List<PedidoEntity> cache;

    public PedidoView(
            PedidoService pedidoService,
            ProdutoService produtoService
    ) {
        this.controller =
                new PedidoController(
                        this,
                        pedidoService,
                        produtoService
                );

        initComponents();
        controller.carregarPedidos();
        controller.carregarProdutos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        listModel = new DefaultListModel<>();
        listaPedidos =
                new JList<>(listModel);

        listaPedidos.setFont(
                Theme.TEXT_FONT
        );

        listaPedidos.setCellRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        Component component =
                                super.getListCellRendererComponent(
                                        list,
                                        value,
                                        index,
                                        isSelected,
                                        cellHasFocus
                                );

                        if (value != null) {
                            String texto =
                                    value.toString();

                            if (texto.contains(
                                    "Status: PAGO"
                            )) {
                                component.setBackground(
                                        new Color(
                                                200,
                                                230,
                                                201
                                        )
                                );

                                component.setForeground(
                                        new Color(
                                                30,
                                                90,
                                                30
                                        )
                                );

                            } else if (texto.contains(
                                    "Status: CANCELADO"
                            )) {
                                component.setBackground(
                                        new Color(
                                                255,
                                                205,
                                                210
                                        )
                                );

                                component.setForeground(
                                        new Color(
                                                150,
                                                30,
                                                30
                                        )
                                );

                            } else {
                                if (isSelected) {
                                    component.setBackground(
                                            list.getSelectionBackground()
                                    );

                                    component.setForeground(
                                            list.getSelectionForeground()
                                    );

                                } else {
                                    component.setBackground(
                                            Color.WHITE
                                    );

                                    component.setForeground(
                                            Color.BLACK
                                    );
                                }
                            }
                        }

                        if (isSelected) {
                            setBorder(
                                    BorderFactory.createLineBorder(
                                            Theme.PRIMARY,
                                            2
                                    )
                            );

                        } else {
                            setBorder(
                                    new EmptyBorder(
                                            8,
                                            10,
                                            8,
                                            10
                                    )
                            );
                        }
                        return component;
                    }
                }
        );

        JScrollPane scroll =
                new JScrollPane(listaPedidos);

        scroll.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // CAMPOS
        clienteIdField =
                createField("Cliente ID");

        pedidoIdField =
                createField(
                        "Pedido ID (selecionado)"
                );

        quantidadeField =
                createField("Quantidade");

        produtoCombo =
                new JComboBox<>();

        produtoCombo.setBorder(
                BorderFactory.createTitledBorder(
                        "Produto"
                )
        );

        // BOTÕES
        JButton criarButton =
                UI.button("Criar Pedido", b -> {
                    b.setBackground(
                            Theme.PRIMARY
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton pagarButton =
                UI.button("Pagar", b -> {
                    b.setBackground(
                            new Color(0, 120, 0)
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton cancelarButton =
                UI.button("Cancelar", b -> {
                    b.setBackground(
                            new Color(200, 60, 60)
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton adicionarItemButton =
                UI.button("Adicionar Item", b -> {
                    b.setBackground(
                            new Color(80, 80, 200)
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton atualizarButton =
                UI.button("Atualizar", b -> {
                    b.setBackground(
                            Theme.PRIMARY_DARK
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton totalButton =
                UI.button("Total", b -> {
                    b.setBackground(
                            new Color(70, 70, 70)
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        JButton limparButton =
                UI.button("Limpar Concluídos", b -> {
                    b.setBackground(
                            new Color(40, 40, 40)
                    );

                    b.setForeground(
                            Color.WHITE
                    );
                });

        // FORMULÁRIO
        JPanel form = UI.panel(p -> {
                    p.setLayout(
                            new GridLayout(
                                    15,
                                    1,
                                    5,
                                    5
                            )
                    );

                    p.setBackground(
                            Theme.SURFACE
                    );

                    p.setBorder(
                            new EmptyBorder(
                                    20,
                                    20,
                                    20,
                                    20
                            )
                    );

                    p.setPreferredSize(
                            new Dimension(
                                    320,
                                    0
                            )
                    );
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
                atualizarButton,
                limparButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        criarButton.addActionListener(
                e -> controller.criarPedido()
        );

        adicionarItemButton.addActionListener(
                e -> controller.adicionarItem()
        );

        pagarButton.addActionListener(
                e -> controller.pagarPedido()
        );

        cancelarButton.addActionListener(
                e -> controller.cancelarPedido()
        );

        totalButton.addActionListener(
                e -> controller.calcularTotal()
        );

        atualizarButton.addActionListener(e -> {
            controller.carregarPedidos();
            controller.carregarProdutos();
        });

        limparButton.addActionListener(
                e -> controller.limparPedidosPainel()
        );

        // SELEÇÃO
        listaPedidos.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });
    }

    private JTextField createField(
            String titulo
    ) {
        JTextField field =
                new JTextField();

        field.setBorder(
                BorderFactory.createTitledBorder(
                        titulo
                )
        );

        field.setFont(
                Theme.TEXT_FONT
        );
        return field;
    }

    private void preencherCampos() {
        PedidoEntity pedido =
                getPedidoSelecionado();

        if (pedido == null) {
            return;
        }

        pedidoIdField.setText(
                String.valueOf(
                        pedido.getId()
                )
        );

        clienteIdField.setText(
                String.valueOf(
                        pedido.getCliente().getId()
                )
        );
    }

    // LIMPAR
    public void clearFields() {
        clienteIdField.setText("");
        pedidoIdField.setText("");
        quantidadeField.setText("");
        listaPedidos.clearSelection();
    }

    public String getQuantidade() {
        return quantidadeField
                .getText()
                .trim();
    }

    public JComboBox<ProdutoEntity>
    getProdutoCombo() {
        return produtoCombo;
    }

    public ProdutoEntity
    getProdutoSelecionado() {
        return (ProdutoEntity)
                produtoCombo.getSelectedItem();
    }

    public DefaultListModel<String>
    getListModel() {
        return listModel;
    }

    public PedidoEntity
    getPedidoSelecionado() {
        int index =
                listaPedidos.getSelectedIndex();

        if (index < 0 ||
                cache == null) {
            return null;
        }
        return cache.get(index);
    }

    // IDS
    public Long requirePedidoId() {
        String texto =
                pedidoIdField
                        .getText()
                        .trim();

        if (texto.isBlank()) {
            throw new IllegalArgumentException(
                    "Selecione um pedido na lista."
            );
        }

        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Pedido inválido selecionado."
            );
        }
    }

    public Long requireClienteId() {
        String texto =
                clienteIdField
                        .getText()
                        .trim();

        if (texto.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o ID do cliente."
            );
        }

        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "ID do cliente inválido."
            );
        }
    }

    public void setCache(
            List<PedidoEntity> cache
    ) {
        this.cache = cache;
    }
}