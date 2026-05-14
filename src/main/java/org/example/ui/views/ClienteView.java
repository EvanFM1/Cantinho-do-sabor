package org.example.ui.views;

import org.example.entity.ClienteEntity;
import org.example.service.ClienteService;
import org.example.ui.Async;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClienteView extends JPanel {
    private final ClienteService clienteService;

    private DefaultListModel<String> listModel;
    private JList<String> listaClientes;

    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField telefoneField;

    private JButton salvarButton;
    private JButton atualizarButton;
    private List<ClienteEntity> cacheClientes;

    public ClienteView(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        loadClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        /*
        Lista
         */
        listModel = new DefaultListModel<>();
        listaClientes = new JList<>(listModel);
        listaClientes.setFont(Theme.TEXT_FONT);
        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        /*
        Formulário
         */
        nomeField = createField("Nome");
        cpfField = createField("CPF");
        telefoneField = createField("Telefone");

        salvarButton = UI.button("Cadastrar", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        atualizarButton = UI.button("Atualizar Lista", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(8, 1, 5, 5));
                    p.setBorder(new EmptyBorder(20, 20, 20, 20));
                    p.setBackground(Theme.SURFACE);
                },
                nomeField,
                cpfField,
                telefoneField,
                salvarButton,
                atualizarButton
        );

        /*
        Layout
         */
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
        Eventos
         */
        salvarButton.addActionListener(e -> salvarCliente());
        atualizarButton.addActionListener(e -> loadClientes());
    }

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(Theme.TEXT_FONT);
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        return field;
    }

    private void salvarCliente() {
        try {
            ClienteEntity c = new ClienteEntity();
            c.setNome(nomeField.getText());
            c.setCpf(cpfField.getText());
            c.setTelefone(telefoneField.getText());

            clienteService.criarCliente(c);
            JOptionPane.showMessageDialog(
                    this,
                    "Cliente cadastrado com sucesso!",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearFields();
            loadClientes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*
    Carregamento Assíncrono
     */
    private void loadClientes() {
        listModel.clear();
        Async.compute(
                clienteService::listarClientes,
                clientes -> {
                    cacheClientes = clientes;

                    for (ClienteEntity c : cacheClientes) {
                        listModel.addElement(
                                "ID: " + c.getId()
                                        + " | " + c.getNome()
                                        + " | CPF: " + c.getCpf()
                        );
                    }
                },
                error -> JOptionPane.showMessageDialog(
                        this,
                        "Erro ao carregar clientes: " + error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    private void clearFields() {
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
    }
}