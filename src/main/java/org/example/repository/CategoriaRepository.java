package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.CategoriaEntity;
import java.util.List;
import java.util.Optional;

public class CategoriaRepository {

    private final EntityManager entityManager;

    public CategoriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public CategoriaEntity salvar(CategoriaEntity categoria) {
        if (categoria.getId() == null) {
            entityManager.persist(categoria);
            return categoria;
        } else {
            return entityManager.merge(categoria);
        }
    }

    public Optional<CategoriaEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(CategoriaEntity.class, id));
    }

    public List<CategoriaEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT c FROM CategoriaEntity c", CategoriaEntity.class)
                .getResultList();
    }

    public void deletar(CategoriaEntity categoria) {
        entityManager.remove(categoria);
    }
}