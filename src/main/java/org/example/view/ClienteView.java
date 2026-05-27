package org.example.view;

import org.example.model.entity.ClienteEntity;
import org.example.model.service.ClienteService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
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
    private JButton editarButton;
    private JButton deletarButton;

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
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

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
                    BorderFactory.createTitledBorder("Telefone")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Botões
        salvarButton = UI.button("Cadastrar", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        atualizarButton = UI.button("Atualizar Lista", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        editarButton = UI.button("Editar", b -> {
            b.setBackground(new Color(255, 170, 0));
            b.setForeground(Color.WHITE);
        });

        deletarButton = UI.button("Deletar", b -> {
            b.setBackground(new Color(200, 60, 60));
            b.setForeground(Color.WHITE);
        });

        /*
        Form
         */
        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(10, 1, 5, 5));
                    p.setBorder(new EmptyBorder(20, 20, 20, 20));
                    p.setBackground(Theme.SURFACE);

                    p.setPreferredSize(new Dimension(300, 0));
                },
                nomeField,
                cpfField,
                telefoneField,

                salvarButton,
                editarButton,
                deletarButton,
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
        Events.mouse(this, mouse -> {
            mouse.onPressed(event -> {
                Component clicked =
                        SwingUtilities.getDeepestComponentAt(
                                ClienteView.this,
                                event.getX(),
                                event.getY()
                        );

                /*
                Deseleciona ao clicar fora da lista
                 */
                if (clicked == null ||
                        !SwingUtilities.isDescendingFrom(clicked, listaClientes)) {
                    listaClientes.clearSelection();
                    clearFields();
                }
            });
        });
        Events.mouse(salvarButton, mouse ->
                mouse.onClicked(e -> salvarCliente())
        );

        Events.mouse(atualizarButton, mouse ->
                mouse.onClicked(e -> loadClientes())
        );

        Events.mouse(editarButton, mouse ->
                mouse.onClicked(e -> editarCliente())
        );

        Events.mouse(deletarButton, mouse ->
                mouse.onClicked(e -> deletarCliente())
        );

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        /*
        Atalhos de teclado
         */
        Events.keyBinder(this, key -> {
        // ENTER -> salvar
            key.on("ENTER", this::salvarCliente);

        // DELETE -> deletar selecionado
            key.on("DELETE", () -> {
                if (listaClientes.getSelectedIndex() >= 0) {
                    deletarCliente();
                }
            });

        // CTRL + R -> atualizar lista
                key.on("ctrl R", this::loadClientes);

        // CTRL + E -> editar cliente
                key.on("ctrl E", () -> {
                    if (listaClientes.getSelectedIndex() >= 0) {
                        editarCliente();
                    }
                });

        // ESC -> limpar seleção
                key.on("ESCAPE", () -> {
                    listaClientes.clearSelection();
                    clearFields();
                });
            });
    }

    private JTextField createField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(Theme.TEXT_FONT);
        field.setBorder(
                BorderFactory.createTitledBorder(placeholder)
        );
        return field;
    }

    // Criar
    private void salvarCliente() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String telefone = telefoneField.getText()
                .replace("_", "")
                .replace(" ", "")
                .replace("-", "")
                .trim();
        if (telefone.isBlank()) {
            telefone = null;
        } else {
            telefone = telefoneField.getText().trim();
        }

        if (nome.isBlank() || cpf.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome e CPF são obrigatórios!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!cpf.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "CPF deve conter exatamente 11 números!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setTelefone(telefone);
        Async.compute(
                () -> {
                    clienteService.criarCliente(cliente);
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cliente cadastrado com sucesso!"
                    );
                    clearFields();
                    loadClientes();
                },

                error -> {
                    String mensagem = error.getMessage();

                    /*
                    CPF duplicado
                     */
                    if (mensagem != null &&
                            mensagem.toLowerCase().contains("cpf")) {

                        mensagem = "CPF já cadastrado!";
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            mensagem,
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
        );
    }

    // Editar
    private void editarCliente() {
        int index = listaClientes.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente!"
            );
            return;
        }

        ClienteEntity cliente = cacheClientes.get(index);
        cliente.setNome(nomeField.getText().trim());
        cliente.setCpf(cpfField.getText().trim());
        String telefone = telefoneField.getText()
                .replace("_", "")
                .replace(" ", "")
                .replace("-", "")
                .trim();
        if (telefone.isBlank()) {
            telefone = null;
        } else {
            telefone = telefoneField.getText().trim();
        }

        cliente.setTelefone(telefone);

        if (cliente.getNome().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nome é obrigatório!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!cliente.getCpf().matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "CPF deve conter exatamente 11 números!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Async.compute(
                () -> {
                    clienteService.atualizarCliente(
                            cliente.getId(),
                            cliente
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cliente atualizado!"
                    );
                    clearFields();
                    loadClientes();
                },

                error -> JOptionPane.showMessageDialog(
                        this,
                        error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Deletar
    private void deletarCliente() {
        int index = listaClientes.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente!"
            );
            return;
        }

        ClienteEntity cliente = cacheClientes.get(index);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja deletar " + cliente.getNome() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Async.compute(
                () -> {
                    clienteService.deletarCliente(cliente.getId());
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cliente deletado!"
                    );
                    clearFields();
                    loadClientes();
                },

                error -> JOptionPane.showMessageDialog(
                        this,
                        error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Lista
    private void loadClientes() {
        listModel.clear();
        Async.compute(
                clienteService::listarClientes,
                clientes -> {
                    cacheClientes = clientes;

                    for (ClienteEntity c : clientes) {
                        listModel.addElement(
                                "• ID: " + c.getId()
                                        + " | " + c.getNome()
                                        + " | CPF: " + c.getCpf()
                                        + " | Telefone: " +
                                        (c.getTelefone() == null || c.getTelefone().isBlank()
                                                ? "Sem Telefone"
                                                : c.getTelefone())
                        );
                    }
                },

                error -> JOptionPane.showMessageDialog(
                        this,
                        "Erro ao carregar clientes: "
                                + error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Preenche os campos ao clicar
    private void preencherCampos() {
        int index = listaClientes.getSelectedIndex();

        if (index < 0 || cacheClientes == null) {
            return;
        }

        ClienteEntity cliente = cacheClientes.get(index);
        nomeField.setText(cliente.getNome());
        cpfField.setText(cliente.getCpf());
        telefoneField.setText(cliente.getTelefone());
    }

    private void clearFields() {
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
        listaClientes.clearSelection();
    }
}