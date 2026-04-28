package org.example.service;

import jakarta.persistence.*;
import java.util.List;
import org.example.entity.ClienteEntity;
import org.example.repository.ClienteRepository;

public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EntityManager entityManager;

    public ClienteService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.clienteRepository = new ClienteRepository(entityManager);
    }

    public ClienteEntity criarCliente(ClienteEntity cliente) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Validações básicas de sorveteria
            if (cliente.getNome() == null || cliente.getNome().isEmpty()) {
                throw new RuntimeException("O nome do cliente é obrigatório!");
            }

            ClienteEntity salvo = clienteRepository.salvar(cliente);

            transaction.commit();
            return salvo;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public ClienteEntity buscarClientePorId(Long id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));
    }

    public List<ClienteEntity> listarClientes() {
        return clienteRepository.listarTodos();
    }

    public ClienteEntity atualizarCliente(Long id, ClienteEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            ClienteEntity cliente = clienteRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

            // Atualizando apenas o que sobrou na entidade Cliente
            cliente.setNome(dadosAtualizados.getNome());
            cliente.setCpf(dadosAtualizados.getCpf());
            cliente.setTelefone(dadosAtualizados.getTelefone());

            ClienteEntity atualizado = clienteRepository.salvar(cliente);

            transaction.commit();
            return atualizado;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarCliente(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            ClienteEntity cliente = clienteRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

            clienteRepository.deletar(cliente);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}