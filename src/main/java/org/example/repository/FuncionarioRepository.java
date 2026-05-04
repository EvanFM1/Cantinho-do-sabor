package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.FuncionarioEntity;
import java.util.*;

public class FuncionarioRepository {

    private final EntityManager entityManager;

    public FuncionarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public FuncionarioEntity salvar(FuncionarioEntity funcionario) {
        if (funcionario.getId() == null) {
            entityManager.persist(funcionario);
            return funcionario;
        } else {
            return entityManager.merge(funcionario);
        }
    }

    public Optional<FuncionarioEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(FuncionarioEntity.class, id));
    }

    public List<FuncionarioEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT f FROM FuncionarioEntity f", FuncionarioEntity.class)
                .getResultList();
    }

    public void deletar(FuncionarioEntity funcionario) {
        if (!entityManager.contains(funcionario)) {
            funcionario = entityManager.merge(funcionario);
        }
        entityManager.remove(funcionario);
    }
}