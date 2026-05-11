package org.example.service;

import jakarta.persistence.*;
import org.example.entity.ClienteEntity;
import org.example.entity.FuncionarioEntity;
import org.example.entity.PedidoEntity;
import org.example.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {
    private final EntityManager entityManager;
    private final PedidoRepository pedidoRepository;

    public PedidoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.pedidoRepository = new PedidoRepository(entityManager);
    }

    public List<PedidoEntity> listarPedidosPorStatus(String status) {
        String jpql = "SELECT p FROM PedidoEntity p JOIN FETCH p.cliente WHERE p.status = :status";
        return entityManager.createQuery(jpql, PedidoEntity.class).setParameter("status", status).getResultList();
    }

    public List<PedidoEntity> listarPedidosPendentes() {
        String jpql = "SELECT p FROM PedidoEntity p " +
                "JOIN FETCH p.cliente " +
                "WHERE p.status = 'ABERTO'";

        return entityManager.createQuery(jpql, PedidoEntity.class).getResultList();
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
            pedido.setStatus("ABERTO");

            pedidoRepository.salvar(pedido);

            transaction.commit();
            return pedido;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public void cancelarPedido(Long pedidoId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            PedidoEntity pedido = pedidoRepository.buscarPorId(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado!"));
            if ("CANCELADO".equals(pedido.getStatus())) {
                throw new RuntimeException("Este pedido já está cancelado!");
            }

            if ("PAGO".equals(pedido.getStatus())) {
                throw new RuntimeException("Não pode cancelar o que já foi pago!");
            }
            pedido.setStatus("CANCELADO");

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public PedidoEntity buscarCompleto(Long id) {
        String jpql = "SELECT p FROM PedidoEntity p " +
                "JOIN FETCH p.cliente " +
                "JOIN FETCH p.funcionario " +
                "WHERE p.id = :id";
        try {
            return entityManager.createQuery(jpql, PedidoEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Pedido não encontrado!");
        }
    }
}