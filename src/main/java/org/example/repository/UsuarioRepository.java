package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.UsuarioEntity;
import java.util.*;

public class UsuarioRepository {
    private final EntityManager entityManager;

    public UsuarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public UsuarioEntity salvar(UsuarioEntity usuario) {
        if (usuario.getId() == null) {
            entityManager.persist(usuario);
            return usuario;
        } else {
            return entityManager.merge(usuario);
        }
    }

    public Optional<UsuarioEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(UsuarioEntity.class, id));
    }

    public List<UsuarioEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT u FROM UsuarioEntity u", UsuarioEntity.class)
                .getResultList();
    }

    public Optional<UsuarioEntity> buscarPorLogin(String login) {
        String jpql = "SELECT u FROM UsuarioEntity u WHERE u.login = :login";
        return entityManager.createQuery(jpql, UsuarioEntity.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst();
    }

    public void deletar(UsuarioEntity usuario) {
        if (!entityManager.contains(usuario)) {
            usuario = entityManager.merge(usuario);
        }
        entityManager.remove(usuario);
    }
}