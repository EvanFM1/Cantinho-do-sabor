package org.example.service;

import jakarta.persistence.*;
import org.example.entity.ClienteEntity;
import org.example.entity.FuncionarioEntity;
import org.example.entity.PedidoEntity;
import org.example.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {

    private EntityManager entityManager;
    private PedidoRepository pedidoRepository;

    public PedidoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.pedidoRepository = new PedidoRepository(entityManager);
    }

    public PedidoEntity criarPedido(Long clienteId, Long funcionarioId) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            ClienteEntity cliente = entityManager.find(ClienteEntity.class, clienteId);
            FuncionarioEntity funcionario = entityManager.find(FuncionarioEntity.class, funcionarioId);

            if (cliente == null) throw new RuntimeException("Cliente não encontrado!");
            if (funcionario == null) throw new RuntimeException("Funcionário não encontrado!");

            PedidoEntity pedido = new PedidoEntity();
            pedido.setCliente(cliente);
            pedido.setFuncionario(funcionario);
            pedido.setDataHora(LocalDateTime.now());
            pedido.setStatus("PENDENTE");

            pedidoRepository.salvar(pedido);

            transaction.commit();
            return pedido;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public PedidoEntity buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado!"));
    }

    public void finalizarPedido(Long pedidoId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PedidoEntity pedido = buscarPorId(pedidoId);

            if ("PAGO".equals(pedido.getStatus())) {
                throw new RuntimeException("Este pedido já foi pago!");
            }

            pedido.setStatus("PAGO");
            pedidoRepository.salvar(pedido);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void cancelarPedido(Long pedidoId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PedidoEntity pedido = buscarPorId(pedidoId);
            pedido.setStatus("CANCELADO");
            pedidoRepository.salvar(pedido);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<PedidoEntity> listarPedidosPorStatus(String status) {
        String jpql = "SELECT p FROM PedidoEntity p WHERE p.status = :status";
        return entityManager.createQuery(jpql, PedidoEntity.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<PedidoEntity> listarPedidosDoCliente(Long clienteId) {
        String jpql = "SELECT p FROM PedidoEntity p WHERE p.cliente.id = :clienteId";
        return entityManager.createQuery(jpql, PedidoEntity.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
    }
}