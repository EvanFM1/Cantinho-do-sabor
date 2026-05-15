package org.example.ui.views;

import org.example.entity.PedidoEntity;
import org.example.service.PedidoService;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.bindings.key.KeyBinder;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PedidoView extends JPanel {
    private final PedidoService pedidoService;

    private DefaultListModel<String> listModel;
    private JList<String> listaPedidos;

    private JTextField clienteIdField;
    private JTextField pedidoIdField;

    private JButton criarButton;
    private JButton cancelarButton;
    private JButton listarButton;
    private JButton totalButton;

    private List<PedidoEntity> cache;

    public PedidoView(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        initComponents();
        loadPedidos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        /*
         * LISTA
         */
        listModel = new DefaultListModel<>();
        listaPedidos = new JList<>(listModel);
        listaPedidos.setFont(Theme.TEXT_FONT);
        listaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(listaPedidos);

        scroll.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );

        /*
         * CAMPOS
         */
        clienteIdField =
                createField("Cliente ID");

        pedidoIdField =
                createField("Pedido ID");

        /*
         * BOTÕES
         */
        criarButton =
                UI.button("Criar Pedido", b -> {
                    b.setBackground(Theme.PRIMARY);
                    b.setForeground(Color.WHITE);
                });

        cancelarButton =
                UI.button("Cancelar Pedido", b -> {
                    b.setBackground(
                            new Color(200, 60, 60)
                    );
                    b.setForeground(Color.WHITE);
                });

        listarButton =
                UI.button("Atualizar Lista", b -> {
                    b.setBackground(
                            Theme.PRIMARY_DARK
                    );
                    b.setForeground(Color.WHITE);
                });

        totalButton =
                UI.button("Calcular Total", b -> {
                    b.setBackground(
                            new Color(70, 70, 70)
                    );
                    b.setForeground(Color.WHITE);
                });

        /*
         * FORMULÁRIO
         */
        JPanel form = UI.panel(p -> {
                    p.setLayout(
                            new GridLayout(10, 1, 5, 5)
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
                            new Dimension(300, 0)
                    );
                },
                clienteIdField,
                pedidoIdField,
                criarButton,
                cancelarButton,
                totalButton,
                listarButton
        );

        /*
         * LAYOUT
         */
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
         * EVENTOS
         */
        criarButton.addActionListener(
                e -> criarPedido()
        );

        cancelarButton.addActionListener(
                e -> cancelarPedido()
        );

        listarButton.addActionListener(
                e -> loadPedidos()
        );

        totalButton.addActionListener(
                e -> calcularTotal()
        );

        /*
         * SELEÇÃO
         */
        listaPedidos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        /*
         * CLICK FORA
         */
        Events.mouse(this, mouse -> {
            mouse.onPressed(event -> {
                Component clicked =
                        SwingUtilities.getDeepestComponentAt(
                                PedidoView.this,
                                event.getX(),
                                event.getY()
                        );

                if (clicked == null
                        ||
                        !SwingUtilities.isDescendingFrom(
                                clicked,
                                listaPedidos
                        )) {
                    listaPedidos.clearSelection();
                    clearFields();
                }
            });
        });

        /*
         * KEYBINDS
         */
        new KeyBinder(this)
                .on("ENTER", this::criarPedido)
                .on("DELETE", () -> {
                    if (listaPedidos.getSelectedIndex() >= 0) {
                        cancelarPedido();
                    }
                })
                .on("ctrl R", this::loadPedidos)
                .on("ctrl T", () -> {
                    if (listaPedidos.getSelectedIndex() >= 0) {
                        calcularTotal();
                    }
                })
                .on("ESCAPE", () -> {
                    listaPedidos.clearSelection();
                    clearFields();
                });
    }

    private JTextField createField(String title) {
        JTextField field =
                new JTextField();

        field.setBorder(
                BorderFactory.createTitledBorder(title)
        );
        field.setFont(Theme.TEXT_FONT);
        return field;
    }

    /*
     * CRIAR
     */
    private void criarPedido() {
        try {
            String clienteIdText =
                    clienteIdField.getText().trim();

            if (clienteIdText.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe o ID do cliente!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Long clienteId =
                    Long.parseLong(clienteIdText);

            pedidoService.criarPedido(clienteId);
            JOptionPane.showMessageDialog(
                    this,
                    "Pedido criado!"
            );

            clearFields();
            loadPedidos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * CANCELAR
     */
    private void cancelarPedido() {
        try {
            String pedidoIdText =
                    pedidoIdField.getText().trim();

            if (pedidoIdText.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe o ID do pedido!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Long id =
                    Long.parseLong(pedidoIdText);

            pedidoService.cancelarPedido(id);
            JOptionPane.showMessageDialog(
                    this,
                    "Pedido cancelado!"
            );
            clearFields();
            loadPedidos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * TOTAL
     */
    private void calcularTotal() {
        try {
            String pedidoIdText =
                    pedidoIdField.getText().trim();

            if (pedidoIdText.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe o ID do pedido!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Long id =
                    Long.parseLong(pedidoIdText);

            var total =
                    pedidoService.calcularTotal(id);

            JOptionPane.showMessageDialog(
                    this,
                    "Total: R$ " + total
            );

        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * LISTAR
     */
    private void loadPedidos() {
        try {
            listModel.clear();
            cache =
                    pedidoService
                            .listarPedidosPendentes();

            for (PedidoEntity p : cache) {
                listModel.addElement(
                        "• ID: " + p.getId()
                                + " | Cliente: "
                                + p.getCliente().getNome()
                                + " | Status: "
                                + p.getStatus()
                );
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * PREENCHER
     */
    private void preencherCampos() {
        int index =
                listaPedidos.getSelectedIndex();

        if (index < 0
                || cache == null) {
            return;
        }

        PedidoEntity pedido =
                cache.get(index);

        pedidoIdField.setText(
                String.valueOf(
                        pedido.getId()
                )
        );

        clienteIdField.setText(
                String.valueOf(
                        pedido.getCliente()
                                .getId()
                )
        );
    }

    /*
     * LIMPAR
     */
    private void clearFields() {
        clienteIdField.setText("");
        pedidoIdField.setText("");
        listaPedidos.clearSelection();
    }

    /*
     * ERRO
     */
    private void showError(Throwable e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}