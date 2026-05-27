package org.example.model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.example.model.entity.CategoriaEntity;
import org.example.model.repository.CategoriaRepository;

import java.math.BigDecimal;
import java.util.List;

public class CategoriaService {
    private final EntityManagerFactory emf;

    public CategoriaService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public CategoriaEntity criarCategoria(CategoriaEntity categoria) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        CategoriaRepository repo = new CategoriaRepository(em);

        try {
            tx.begin();
            validarCategoria(categoria);
            boolean existe = !em.createQuery(
                            "SELECT c FROM CategoriaEntity c WHERE LOWER(c.nome) = LOWER(:nome)",
                            CategoriaEntity.class
                    )
                    .setParameter("nome", categoria.getNome())
                    .getResultList()
                    .isEmpty();

            if (existe) {
                throw new RuntimeException("Categoria já existe!");
            }

            aplicarRegraValor(categoria);
            repo.salvar(categoria);
            tx.commit();
            return categoria;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public CategoriaEntity buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            CategoriaRepository repo = new CategoriaRepository(em);
            return repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));
        } finally {
            em.close();
        }
    }

    public List<CategoriaEntity> listarCategorias() {
        EntityManager em = emf.createEntityManager();
        try {
            CategoriaRepository repo = new CategoriaRepository(em);
            return repo.listarTodos();
        } finally {
            em.close();
        }
    }

    public CategoriaEntity atualizarCategoria(Long id, CategoriaEntity dados) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        CategoriaRepository repo = new CategoriaRepository(em);

        try {
            tx.begin();
            CategoriaEntity categoria = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            categoria.setNome(dados.getNome());
            categoria.setDescricao(dados.getDescricao());
            aplicarRegraValor(categoria);
            CategoriaEntity atualizada = repo.salvar(categoria);
            tx.commit();
            return atualizada;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deletarCategoria(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        CategoriaRepository repo = new CategoriaRepository(em);

        try {
            tx.begin();
            CategoriaEntity categoria = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            repo.deletar(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void validarCategoria(CategoriaEntity categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new RuntimeException("Nome obrigatório!");
        }
    }

    private void aplicarRegraValor(CategoriaEntity categoria) {
        String nome = categoria.getNome().toUpperCase();
        switch (nome) {
            case "PICOLE" -> categoria.setValor(new BigDecimal("0.00"));
            case "PESO" -> categoria.setValor(new BigDecimal("60.00"));
            case "POTE" -> categoria.setValor(new BigDecimal("0.00"));
            default -> throw new RuntimeException("Categoria inválida!");
        }
    }
}