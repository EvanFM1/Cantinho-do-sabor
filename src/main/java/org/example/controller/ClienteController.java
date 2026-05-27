package org.example.controller;

import org.example.model.entity.ClienteEntity;
import org.example.model.service.ClienteService;
import org.example.ui.Async;
import org.example.view.ClienteView;
import javax.swing.*;

public class ClienteController {
    private final ClienteView view;
    private final ClienteService clienteService;

    public ClienteController(
            ClienteView view,
            ClienteService clienteService
    ) {
        this.view = view;
        this.clienteService = clienteService;
    }

    // Criar
    public void salvarCliente() {
        String nome = view.getNome();
        String cpf = view.getCpf();
        String telefone = view.getTelefone();

        if (nome.isBlank() || cpf.isBlank()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Nome e CPF são obrigatórios!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!cpf.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    view,
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
                            view,
                            "Cliente cadastrado com sucesso!"
                    );
                    view.clearFields();
                    carregarClientes();
                },

                error -> {
                    String mensagem = error.getMessage();
                    if (mensagem != null &&
                            mensagem.toLowerCase().contains("cpf")) {
                        mensagem = "CPF já cadastrado!";
                    }

                    JOptionPane.showMessageDialog(
                            view,
                            mensagem,
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
        );
    }

    // Editar
    public void editarCliente() {
        ClienteEntity cliente =
                view.getClienteSelecionado();

        if (cliente == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Selecione um cliente!"
            );
            return;
        }
        cliente.setNome(view.getNome());
        cliente.setCpf(view.getCpf());
        cliente.setTelefone(view.getTelefone());

        if (cliente.getNome().isBlank()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Nome é obrigatório!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!cliente.getCpf().matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    view,
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
                            view,
                            "Cliente atualizado!"
                    );
                    view.clearFields();
                    carregarClientes();
                },

                error -> JOptionPane.showMessageDialog(
                        view,
                        error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Deletar
    public void deletarCliente() {
        ClienteEntity cliente =
                view.getClienteSelecionado();

        if (cliente == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Selecione um cliente!"
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Deseja deletar " + cliente.getNome() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Async.compute(
                () -> {
                    clienteService.deletarCliente(
                            cliente.getId()
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            view,
                            "Cliente deletado!"
                    );
                    view.clearFields();
                    carregarClientes();
                },

                error -> JOptionPane.showMessageDialog(
                        view,
                        error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Lista
    public void carregarClientes() {
        Async.compute(
                clienteService::listarClientes,
                view::atualizarLista,

                error -> JOptionPane.showMessageDialog(
                        view,
                        "Erro ao carregar clientes: "
                                + error.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }
}