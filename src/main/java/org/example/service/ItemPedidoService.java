package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.entity.ItemPedidoEntity;
import org.example.entity.PedidoEntity;
import org.example.entity.ProdutoEntity;
import org.example.repository.ItemPedidoRepository;

import java.util.List;

public class ItemPedidoService {

    private final EntityManager entityManager;
    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.itemPedidoRepository = new ItemPedidoRepository(entityManager);
    }

    public void adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Busca as entidades necessárias
            PedidoEntity pedido = entityManager.find(PedidoEntity.class, pedidoId);
            ProdutoEntity produto = entityManager.find(ProdutoEntity.class, produtoId);

            if (pedido == null) throw new RuntimeException("Pedido não encontrado!");
            if (produto == null) throw new RuntimeException("Produto não encontrado!");
            if (!"ABERTO".equals(pedido.getStatus())) throw new RuntimeException("Pedido não está aberto!");

            // CÁLCULO AUTOMÁTICO: Pega o preço direto do cadastro do produto
            ItemPedidoEntity item = new ItemPedidoEntity();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco()); // Aqui o sistema "puxa" o valor sozinho

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