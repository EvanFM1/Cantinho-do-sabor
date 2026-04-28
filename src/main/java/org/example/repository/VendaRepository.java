package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.VendaEntity;
import java.util.List;
import java.util.Optional;

public class VendaRepository {

    private final EntityManager entityManager;

    public VendaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public VendaEntity salvar(VendaEntity venda) {
        if (venda.getId() == null) {
            entityManager.persist(venda);
            return venda;
        } else {
            return entityManager.merge(venda);
        }
    }

    public Optional<VendaEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(VendaEntity.class, id));
    }

    public List<VendaEntity> listarTodas() {
        return entityManager
                .createQuery("SELECT v FROM VendaEntity v", VendaEntity.class)
                .getResultList();
    }

    public void deletar(VendaEntity venda) {
        if (!entityManager.contains(venda)) {
            venda = entityManager.merge(venda);
        }
        entityManager.remove(venda);
    }
}