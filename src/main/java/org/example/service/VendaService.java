package org.example.service;

import jakarta.persistence.*;
import org.example.entity.PedidoEntity;
import org.example.entity.VendaEntity;
import org.example.repository.VendaRepository;
import org.example.entity.ItemPedidoEntity;
import org.example.entity.ProdutoEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendaService {
    private EntityManager entityManager;
    private VendaRepository vendaRepository;

    public VendaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.vendaRepository = new VendaRepository(entityManager);
    }

    public VendaEntity criarVenda(Long pedidoId, BigDecimal valorTotal, String metodoPagamento) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            PedidoEntity pedido = entityManager.find(PedidoEntity.class, pedidoId);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado!");
            }

            if ("CANCELADO".equalsIgnoreCase(pedido.getStatus())) {
                throw new RuntimeException("ERRO: Não é possível vender um pedido que foi CANCELADO!");
            }

            if ("PAGO".equalsIgnoreCase(pedido.getStatus()) || "FINALIZADO".equalsIgnoreCase(pedido.getStatus())) {
                throw new RuntimeException("ERRO: Este pedido já foi pago!");
            }

            VendaEntity venda = new VendaEntity();
            venda.setPedido(pedido);
            venda.setValorTotal(valorTotal);
            venda.setMetodoPagamento(metodoPagamento);
            venda.setStatus("PENDENTE");
            venda.setDataVenda(LocalDateTime.now());

            vendaRepository.salvar(venda);

            transaction.commit();
            return venda;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public VendaEntity finalizarVenda(Long vendaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            VendaEntity venda = vendaRepository.buscarPorId(vendaId)
                    .orElseThrow(() -> new RuntimeException("Venda não encontrada!"));

            if ("PAGA".equals(venda.getStatus())) {
                throw new RuntimeException("Esta venda já foi finalizada!");
            }
            PedidoEntity pedido = venda.getPedido();

            // Iteramos sobre os itens do pedido (certifique-se de ter a List<ItemPedidoEntity> no PedidoEntity)
            if (pedido.getItens() != null) {
                for (ItemPedidoEntity item : pedido.getItens()) {
                    ProdutoEntity produto = item.getProduto();
                    int quantidadeVendida = item.getQuantidade();

                    if (produto.getEstoque() < quantidadeVendida) {
                        throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                    }

                    // Subtrai do estoque
                    produto.setEstoque(produto.getEstoque() - quantidadeVendida);

                    // O Hibernate entenderá que o objeto 'produto' foi alterado
                    // e fará o UPDATE automaticamente ao comitar a transação.
                }
            }
            venda.setStatus("PAGA");
            venda.setDataVenda(LocalDateTime.now());

            venda.getPedido().setStatus("PAGO");

            transaction.commit();
            return venda;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public VendaEntity buscarPorId(Long id) {
        return vendaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada!"));
    }

    public List<VendaEntity> listarVendas() {
        return vendaRepository.listarTodas();
    }

    public List<VendaEntity> buscarPorMetodoPagamento(String metodo) {
        String jpql = "SELECT v FROM VendaEntity v WHERE v.metodoPagamento = :metodo";
        return entityManager.createQuery(jpql, VendaEntity.class)
                .setParameter("metodo", metodo)
                .getResultList();
    }

    public void deletarVenda(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            VendaEntity venda = vendaRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Venda não encontrada!"));
            vendaRepository.deletar(venda);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }
}