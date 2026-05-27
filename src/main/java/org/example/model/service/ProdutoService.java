package org.example.model.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.example.model.entity.CategoriaEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {
    private final EntityManagerFactory emf;

    public ProdutoService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public ProdutoEntity criarProduto(ProdutoEntity produto, Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ProdutoRepository repo = new ProdutoRepository(em);

        try {
            tx.begin();
            if (produto.getNome() == null || produto.getNome().isBlank()) {
                throw new RuntimeException("Nome do produto é obrigatório!");
            }

            if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Preço deve ser maior que zero!");
            }

            CategoriaEntity categoria = em.find(CategoriaEntity.class, categoriaId);
            if (categoria == null) {
                throw new RuntimeException("Categoria não encontrada!");
            }

            produto.setCategoria(categoria);
            repo.salvar(produto);
            tx.commit();
            return produto;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public ProdutoEntity buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            ProdutoRepository repo = new ProdutoRepository(em);

            return repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
        } finally {
            em.close();
        }
    }

    public List<ProdutoEntity> listarProdutos() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM ProdutoEntity p JOIN FETCH p.categoria",
                    ProdutoEntity.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void adicionarEstoque(Long id, BigDecimal quantidade) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ProdutoRepository repo = new ProdutoRepository(em);

        try {
            tx.begin();
            ProdutoEntity produto = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Quantidade deve ser maior que zero!");
            }

            produto.setEstoque(produto.getEstoque().add(quantidade));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void removerEstoque(Long id, BigDecimal quantidade) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ProdutoRepository repo = new ProdutoRepository(em);

        try {
            tx.begin();
            ProdutoEntity produto = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Quantidade deve ser positiva!");
            }

            if (produto.getEstoque().compareTo(quantidade) < 0) {
                throw new RuntimeException("Estoque insuficiente!");
            }

            produto.setEstoque(produto.getEstoque().subtract(quantidade));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deletarProduto(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        ProdutoRepository repo = new ProdutoRepository(em);

        try {
            tx.begin();
            ProdutoEntity produto = repo.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            repo.deletar(produto);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}