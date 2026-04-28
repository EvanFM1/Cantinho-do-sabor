package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.PedidoEntity;
import java.util.List;
import java.util.Optional;

public class PedidoRepository {

    private final EntityManager entityManager;

    public PedidoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PedidoEntity salvar(PedidoEntity pedido) {
        if (pedido.getId() == null) {
            entityManager.persist(pedido);
            return pedido;
        } else {
            return entityManager.merge(pedido);
        }
    }

    public Optional<PedidoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(PedidoEntity.class, id));
    }

    public List<PedidoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT p FROM PedidoEntity p", PedidoEntity.class)
                .getResultList();
    }

    public void deletar(PedidoEntity pedido) {
        if (!entityManager.contains(pedido)) {
            pedido = entityManager.merge(pedido);
        }
        entityManager.remove(pedido);
    }
}