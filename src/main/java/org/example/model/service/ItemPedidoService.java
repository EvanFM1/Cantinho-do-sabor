package org.example.model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.model.entity.ItemPedidoEntity;
import org.example.model.entity.PedidoEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.repository.ItemPedidoRepository;
import java.math.BigDecimal;

import java.util.List;

public class ItemPedidoService {

    private final EntityManager entityManager;
    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.itemPedidoRepository = new ItemPedidoRepository(entityManager);
    }

    public void adicionarItem(Long pedidoId, Long produtoId, BigDecimal quantidade) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Busca as entidades necessárias
            PedidoEntity pedido = entityManager.find(PedidoEntity.class, pedidoId);
            ProdutoEntity produto = entityManager.find(ProdutoEntity.class, produtoId);

            BigDecimal estoqueAtual = produto.getEstoque();
            if (quantidade.compareTo(estoqueAtual) > 0) {
                throw new RuntimeException("Estoque insuficiente! Disponível: " + estoqueAtual);
            }

            // CÁLCULO AUTOMÁTICO: Pega o preço direto do cadastro do produto
            ItemPedidoEntity item = new ItemPedidoEntity();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());

            itemPedidoRepository.salvar(item);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public List<ItemPedidoEntity> listarItensPorPedido(Long pedidoId) {
        String jpql = "SELECT i FROM ItemPedidoEntity i WHERE i.pedido.id = :pedidoId";
        return entityManager.createQuery(jpql, ItemPedidoEntity.class)
                .setParameter("pedidoId", pedidoId)
                .getResultList();
    }
}