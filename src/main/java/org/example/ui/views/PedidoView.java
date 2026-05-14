package org.example.ui.views;

import org.example.entity.PedidoEntity;
import org.example.service.PedidoService;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
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

        JScrollPane scroll = new JScrollPane(listaPedidos);

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

        cancelarButton = UI.button("Cancelar", b -> {
            b.setBackground(Color.RED);
            b.setForeground(Color.WHITE);
        });

        listarButton = UI.button("Atualizar", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        totalButton = UI.button("Calcular Total", b -> {
            b.setBackground(new Color(80, 80, 80));
            b.setForeground(Color.WHITE);
        });

        /*
        Formulário
         */
        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(8, 1, 5, 5));
                    p.setBackground(Theme.SURFACE);
                },
                clienteIdField,
                pedidoIdField,
                criarButton,
                cancelarButton,
                listarButton,
                totalButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
        Eventos
         */
        criarButton.addActionListener(e -> criarPedido());
        cancelarButton.addActionListener(e -> cancelarPedido());
        listarButton.addActionListener(e -> loadPedidos());
        totalButton.addActionListener(e -> calcularTotal());
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setFont(Theme.TEXT_FONT);
        return field;
    }

    private void criarPedido() {
        try {
            if (clienteIdField.getText().isBlank()) {
                throw new RuntimeException("Informe o ID do cliente");
            }

            Long clienteId = Long.parseLong(clienteIdField.getText());
            pedidoService.criarPedido(clienteId);
            JOptionPane.showMessageDialog(this, "Pedido criado!");
            loadPedidos();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void cancelarPedido() {
        try {
            if (pedidoIdField.getText().isBlank()) {
                throw new RuntimeException("Informe o ID do pedido");
            }

            Long id = Long.parseLong(pedidoIdField.getText());
            pedidoService.cancelarPedido(id);
            JOptionPane.showMessageDialog(this, "Pedido cancelado!");
            loadPedidos();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void calcularTotal() {
        try {
            if (pedidoIdField.getText().isBlank()) {
                throw new RuntimeException("Informe o ID do pedido");
            }

            Long id = Long.parseLong(pedidoIdField.getText());
            var total = pedidoService.calcularTotal(id);
            JOptionPane.showMessageDialog(
                    this,
                    "Total: R$ " + total
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadPedidos() {
        listModel.clear();

        List<PedidoEntity> pedidos =
                pedidoService.listarPedidosPendentes();
        cache = pedidos;

        for (PedidoEntity p : pedidos) {
            listModel.addElement(
                    "ID: " + p.getId()
                            + " | Cliente: " + p.getCliente().getNome()
                            + " | Status: " + p.getStatus()
            );
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}