package org.example.ui.views;

import org.example.entity.PedidoEntity;
import org.example.service.PedidoService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
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
        Lista
         */
        listModel = new DefaultListModel<>();
        listaPedidos = new JList<>(listModel);
        listaPedidos.setFont(Theme.TEXT_FONT);
        listaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        JScrollPane scroll = new JScrollPane(listaPedidos);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        /*
        Campos
         */
        clienteIdField = createField("Cliente ID");
        pedidoIdField = createField("Pedido ID");

        /*
        Botões
         */
        criarButton = UI.button("Criar Pedido", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        cancelarButton = UI.button("Cancelar Pedido", b -> {
            b.setBackground(new Color(200, 60, 60));
            b.setForeground(Color.WHITE);
        });

        listarButton = UI.button("Atualizar Lista", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        totalButton = UI.button("Calcular Total", b -> {
            b.setBackground(new Color(70, 70, 70));
            b.setForeground(Color.WHITE);
        });

        /*
        Formulário
         */
        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(10, 1, 5, 5));
                    p.setBackground(Theme.SURFACE);

                    p.setBorder(
                            new EmptyBorder(20, 20, 20, 20)
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
        Layout
         */
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
        Eventos Mouse
         */
        Events.mouse(criarButton, mouse ->
                mouse.onClicked(e -> criarPedido())
        );

        Events.mouse(cancelarButton, mouse ->
                mouse.onClicked(e -> cancelarPedido())
        );

        Events.mouse(listarButton, mouse ->
                mouse.onClicked(e -> loadPedidos())
        );

        Events.mouse(totalButton, mouse ->
                mouse.onClicked(e -> calcularTotal())
        );

        /*
        Selecionar pedido
         */
        listaPedidos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        /*
        Deseleciona ao clicar fora
         */
        Events.mouse(this, mouse -> {
            mouse.onPressed(event -> {
                Component clicked =
                        SwingUtilities.getDeepestComponentAt(
                                PedidoView.this,
                                event.getX(),
                                event.getY()
                        );

                if (clicked == null ||
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
        Atalhos teclado
         */
        Events.keyBinder(this, key -> {
            // ENTER -> criar pedido
            key.on("ENTER", this::criarPedido);

            // DELETE -> cancelar pedido
            key.on("DELETE", () -> {

                if (listaPedidos.getSelectedIndex() >= 0) {
                    cancelarPedido();
                }
            });

            // CTRL + R -> atualizar
            key.on("ctrl R", this::loadPedidos);

            // CTRL + T -> calcular total
            key.on("ctrl T", () -> {

                if (listaPedidos.getSelectedIndex() >= 0) {
                    calcularTotal();
                }
            });

            // ESC -> limpar seleção
            key.on("ESCAPE", () -> {
                listaPedidos.clearSelection();
                clearFields();
            });
        });
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();

        field.setBorder(
                BorderFactory.createTitledBorder(title)
        );
        field.setFont(Theme.TEXT_FONT);
        return field;
    }

    /*
    Criar pedido
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

            Async.compute(
                    () -> {
                        pedidoService.criarPedido(clienteId);
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Pedido criado!"
                        );
                        clearFields();
                        loadPedidos();
                    },
                    error -> showError(error)
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Cancelar pedido
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

            Long id = Long.parseLong(pedidoIdText);
            Async.compute(
                    () -> {
                        pedidoService.cancelarPedido(id);
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Pedido cancelado!"
                        );
                        clearFields();
                        loadPedidos();
                    },
                    error -> showError(error)
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Calcular total
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

            Long id = Long.parseLong(pedidoIdText);
            Async.compute(
                    () -> pedidoService.calcularTotal(id),

                    total -> JOptionPane.showMessageDialog(
                            this,
                            "Total: R$ " + total
                    ),
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Carregar pedidos
     */
    private void loadPedidos() {
        listModel.clear();

        Async.compute(
                pedidoService::listarPedidosPendentes,
                pedidos -> {

                    cache = pedidos;
                    for (PedidoEntity p : pedidos) {
                        listModel.addElement(
                                "• ID: " + p.getId()
                                        + " | Cliente: "
                                        + p.getCliente().getNome()
                                        + " | Status: "
                                        + p.getStatus()
                        );
                    }
                },
                this::showError
        );
    }

    /*
    Preenche campos ao selecionar
     */
    private void preencherCampos() {
        int index = listaPedidos.getSelectedIndex();

        if (index < 0 || cache == null) {
            return;
        }

        PedidoEntity pedido = cache.get(index);
        pedidoIdField.setText(
                String.valueOf(pedido.getId())
        );

        clienteIdField.setText(
                String.valueOf(
                        pedido.getCliente().getId()
                )
        );
    }

    private void clearFields() {
        clienteIdField.setText("");
        pedidoIdField.setText("");
        listaPedidos.clearSelection();
    }

    private void showError(Throwable e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}