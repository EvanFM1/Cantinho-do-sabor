package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.example.entity.ClienteEntity;
import org.example.repository.ClienteRepository;

import javax.swing.*;
import java.util.List;

public class ClienteService {
    private final EntityManagerFactory emf;

    public ClienteService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public ClienteEntity criarCliente(ClienteEntity cliente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ClienteRepository repo = new ClienteRepository(em);

        try {
            tx.begin();

            if (cliente.getNome() == null || cliente.getNome().isBlank()) {
                throw new RuntimeException("O nome do cliente é obrigatório!");
            }

            if (cliente.getCpf() == null || !cliente.getCpf().matches("\\d{11}")) {
                throw new RuntimeException("CPF deve conter exatamente 11 números!");
            }

            if (cliente.getTelefone() != null &&
                    !cliente.getTelefone().isBlank() &&
                    !cliente.getTelefone().matches("\\d{2}\\s\\d{5}-\\d{4}")) {

                throw new RuntimeException(
                        "Telefone deve estar no formato: 45 99999-9999"
                );
            }

            ClienteEntity salvo = repo.salvar(cliente);
            tx.commit();
            return salvo;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public ClienteEntity buscarClientePorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            ClienteRepository repo = new ClienteRepository(em);

            return repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));
        } finally {
            em.close();
        }
    }

    public List<ClienteEntity> listarClientes() {
        EntityManager em = emf.createEntityManager();

        try {
            ClienteRepository repo = new ClienteRepository(em);
            return repo.listarTodos();
        } finally {
            em.close();
        }
    }

    public ClienteEntity atualizarCliente(Long id, ClienteEntity dados) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ClienteRepository repo = new ClienteRepository(em);

        try {
            tx.begin();
            ClienteEntity cliente = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

            if (dados.getNome() == null || dados.getNome().isBlank()) {
                throw new RuntimeException("O nome do cliente é obrigatório!");
            }

            if (dados.getCpf() == null || !dados.getCpf().matches("\\d{11}")) {
                throw new RuntimeException("CPF deve conter exatamente 11 números!");
            }

            if (dados.getTelefone() != null &&
                    !dados.getTelefone().isBlank() &&
                    !dados.getTelefone().matches("\\d{2}\\s\\d{5}-\\d{4}")) {

                throw new RuntimeException(
                        "Telefone deve estar no formato: XX 99999-9999"
                );
            }

            cliente.setNome(dados.getNome());
            cliente.setCpf(dados.getCpf());
            cliente.setTelefone(dados.getTelefone());
            ClienteEntity atualizado = repo.salvar(cliente);
            tx.commit();
            return atualizado;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deletarCliente(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ClienteRepository repo = new ClienteRepository(em);

        try {
            tx.begin();
            ClienteEntity cliente = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

            repo.deletar(cliente);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}