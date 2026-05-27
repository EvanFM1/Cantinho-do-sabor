package org.example.model.repository;

import jakarta.persistence.EntityManager;
import org.example.model.entity.ProdutoEntity;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    private final EntityManager entityManager;

    public ProdutoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ProdutoEntity salvar(ProdutoEntity produto) {
        if (produto.getId() == null) {
            entityManager.persist(produto);
            return produto;
        } else {
            return entityManager.merge(produto);
        }
    }

    public Optional<ProdutoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(ProdutoEntity.class, id));
    }

    public List<ProdutoEntity> listarTodos() {
        return entityManager
                .createQuery(
                        "SELECT p FROM ProdutoEntity p JOIN FETCH p.categoria",
                        ProdutoEntity.class
                )
                .getResultList();
    }

    public void deletar(ProdutoEntity produto) {
        if (!entityManager.contains(produto)) {
            produto = entityManager.merge(produto);
        }
        entityManager.remove(produto);
    }
}