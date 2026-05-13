package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.ItemPedidoEntity;
import java.util.List;
import java.util.Optional;

public class ItemPedidoRepository {

    private final EntityManager entityManager;

    public ItemPedidoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ItemPedidoEntity salvar(ItemPedidoEntity item) {
        if (item.getId() == null) {
            entityManager.persist(item);
            return item;
        } else {
            return entityManager.merge(item);
        }
    }

    public Optional<ItemPedidoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(ItemPedidoEntity.class, id));
    }

    public List<ItemPedidoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT i FROM ItemPedidoEntity i", ItemPedidoEntity.class)
                .getResultList();
    }

    public void deletar(ItemPedidoEntity item) {
        if (!entityManager.contains(item)) {
            item = entityManager.merge(item);
        }
        entityManager.remove(item);
    }
}