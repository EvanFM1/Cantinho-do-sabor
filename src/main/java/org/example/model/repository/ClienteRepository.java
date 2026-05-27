package org.example.model.repository;

import jakarta.persistence.EntityManager;
import org.example.model.entity.ClienteEntity;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    private final EntityManager entityManager;

    public ClienteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ClienteEntity salvar(ClienteEntity cliente) {
        if (cliente.getId() == null) {
            entityManager.persist(cliente);
            return cliente;
        } else {
            return entityManager.merge(cliente);
        }
    }

    public Optional<ClienteEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(ClienteEntity.class, id));
    }

    public List<ClienteEntity> listarTodos() {
        return entityManager
                .createQuery(
                        "SELECT c FROM ClienteEntity c ORDER BY c.id ASC",
                        ClienteEntity.class
                )
                .getResultList();
    }

    public void deletar(ClienteEntity cliente) {
        entityManager.remove(cliente);
    }
}