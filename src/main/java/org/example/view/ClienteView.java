package org.example.view;

import org.example.controller.ClienteController;
import org.example.model.entity.ClienteEntity;
import org.example.model.service.ClienteService;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.util.List;

public class ClienteView extends JPanel {
    private final ClienteController controller;

    private DefaultListModel<String> listModel;
    private JList<String> listaClientes;

    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField telefoneField;

    private List<ClienteEntity> cacheClientes;

    public ClienteView(ClienteService clienteService) {
        this.controller =
                new ClienteController(this, clienteService);

        initComponents();
        controller.carregarClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        // Lista
        listModel = new DefaultListModel<>();
        listaClientes = new JList<>(listModel);
        listaClientes.setFont(Theme.TEXT_FONT);
        listaClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(listaClientes);

        scroll.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );

        // Campos
        nomeField = createField("Nome");
        cpfField = createField("CPF");

        try {
            MaskFormatter telefoneMask =
                    new MaskFormatter("## #####-####");

            telefoneMask.setPlaceholderCharacter('_');
            telefoneField =
                    new JFormattedTextField(telefoneMask);

            telefoneField.setFont(Theme.TEXT_FONT);
            telefoneField.setBorder(
                    BorderFactory.createTitledBorder(
                            "Telefone"
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Botões
        JButton salvarButton = UI.button("Cadastrar", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        JButton atualizarButton = UI.button("Atualizar Lista", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        JButton editarButton = UI.button("Editar", b -> {
            b.setBackground(new Color(255, 170, 0));
            b.setForeground(Color.WHITE);
        });

        JButton deletarButton = UI.button("Deletar", b -> {
            b.setBackground(new Color(200, 60, 60));
            b.setForeground(Color.WHITE);
        });

        // Form
        JPanel form = UI.panel(p -> {
                    p.setLayout(
                            new GridLayout(10, 1, 5, 5)
                    );

                    p.setBorder(
                            new EmptyBorder(
                                    20,
                                    20,
                                    20,
                                    20
                            )
                    );

                    p.setBackground(Theme.SURFACE);
                    p.setPreferredSize(
                            new Dimension(300, 0)
                    );
                },

                nomeField,
                cpfField,
                telefoneField,

                salvarButton,
                editarButton,
                deletarButton,
                atualizarButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        // Eventos
        Events.mouse(this, mouse -> mouse.onPressed(event -> {
            Component clicked =
                    SwingUtilities
                            .getDeepestComponentAt(
                                    ClienteView.this,
                                    event.getX(),
                                    event.getY()
                            );

            // Deseleciona ao clicar fora
            if (clicked == null ||
                    !SwingUtilities.isDescendingFrom(
                            clicked,
                            listaClientes
                    )) {

                listaClientes.clearSelection();
                clearFields();
            }
        }));

        Events.mouse(salvarButton, mouse ->
                mouse.onClicked(
                        e -> controller.salvarCliente()
                )
        );

        Events.mouse(atualizarButton, mouse ->
                mouse.onClicked(
                        e -> controller.carregarClientes()
                )
        );

        Events.mouse(editarButton, mouse ->
                mouse.onClicked(
                        e -> controller.editarCliente()
                )
        );

        Events.mouse(deletarButton, mouse ->
                mouse.onClicked(
                        e -> controller.deletarCliente()
                )
        );

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        // Atalhos
        Events.keyBinder(this, key -> {

            // ENTER
            key.on(
                    "ENTER",
                    controller::salvarCliente
            );

            // DELETE
            key.on("DELETE", () -> {
                if (listaClientes
                        .getSelectedIndex() >= 0) {
                    controller.deletarCliente();
                }
            });

            // CTRL + R
            key.on(
                    "ctrl R",
                    controller::carregarClientes
            );

            // CTRL + E
            key.on("ctrl E", () -> {
                if (listaClientes
                        .getSelectedIndex() >= 0) {
                    controller.editarCliente();
                }
            });

            // ESC
            key.on("ESCAPE", () -> {
                listaClientes.clearSelection();
                clearFields();
            });
        });
    }

    private JTextField createField(
            String placeholder
    ) {
        JTextField field = new JTextField();
        field.setFont(Theme.TEXT_FONT);
        field.setBorder(
                BorderFactory.createTitledBorder(
                        placeholder
                )
        );
        return field;
    }

    public void atualizarLista(
            List<ClienteEntity> clientes
    ) {
        cacheClientes = clientes;
        listModel.clear();

        for (ClienteEntity c : clientes) {

            listModel.addElement(
                    "• ID: " + c.getId()
                            + " | " + c.getNome()
                            + " | CPF: " + c.getCpf()
                            + " | Telefone: " +
                            (c.getTelefone() == null ||
                                    c.getTelefone().isBlank()
                                    ? "Sem Telefone"
                                    : c.getTelefone())
            );
        }
    }

    private void preencherCampos() {
        ClienteEntity cliente =
                getClienteSelecionado();

        if (cliente == null) {
            return;
        }

        nomeField.setText(cliente.getNome());
        cpfField.setText(cliente.getCpf());
        telefoneField.setText(
                cliente.getTelefone()
        );
    }

    public void clearFields() {
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
        listaClientes.clearSelection();
    }

    public String getNome() {
        return nomeField.getText().trim();
    }

    public String getCpf() {
        return cpfField.getText().trim();
    }

    public String getTelefone() {
        String telefone =
                telefoneField.getText()

                        .replace("_", "")
                        .replace(" ", "")
                        .replace("-", "")
                        .trim();
        if (telefone.isBlank()) {
            return null;
        }

        return telefoneField
                .getText()
                .trim();
    }

    public ClienteEntity getClienteSelecionado() {
        int index =
                listaClientes.getSelectedIndex();

        if (index < 0 ||
                cacheClientes == null) {

            return null;
        }
        return cacheClientes.get(index);
    }
}