package org.example.service;

import jakarta.persistence.*;
import org.example.entity.CategoriaEntity;
import org.example.entity.ProdutoEntity;
import org.example.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {

    private EntityManager entityManager;
    private ProdutoRepository produtoRepository;

    public ProdutoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.produtoRepository = new ProdutoRepository(entityManager);
    }

    public ProdutoEntity criarProduto(ProdutoEntity produto, Long categoriaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            if (produto.getNome() == null || produto.getNome().isEmpty()) {
                throw new RuntimeException("Nome do produto é obrigatório!");
            }

            if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Preço deve ser maior que zero!");
            }

            CategoriaEntity categoria = entityManager.find(CategoriaEntity.class, categoriaId);

            if (categoria == null) {
                throw new RuntimeException("Categoria não encontrada!");
            }

            produto.setCategoria(categoria);
            produtoRepository.salvar(produto);

            transaction.commit();
            return produto;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public ProdutoEntity buscarPorId(Long id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));
    }

    public List<ProdutoEntity> listarProdutos() {
        return produtoRepository.listarTodos();
    }

    public ProdutoEntity atualizarProduto(Long id, ProdutoEntity dadosAtualizados, Long categoriaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            ProdutoEntity produto = produtoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            produto.setNome(dadosAtualizados.getNome());
            produto.setDescricao(dadosAtualizados.getDescricao());

            if (categoriaId != null) {
                CategoriaEntity categoria = entityManager.find(CategoriaEntity.class, categoriaId);
                if (categoria == null) {
                    throw new RuntimeException("Categoria não encontrada!");
                }
                produto.setCategoria(categoria);
            }

            ProdutoEntity atualizado = produtoRepository.salvar(produto);
            transaction.commit();
            return atualizado;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarProduto(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            ProdutoEntity produto = produtoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

            produtoRepository.deletar(produto);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}