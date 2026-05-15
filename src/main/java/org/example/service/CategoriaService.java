package org.example.service;

import jakarta.persistence.*;
import org.example.entity.CategoriaEntity;
import org.example.repository.CategoriaRepository;
import java.math.BigDecimal;
import java.util.List;

public class CategoriaService {

    private EntityManager entityManager;
    private CategoriaRepository categoriaRepository;

    public CategoriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.categoriaRepository = new CategoriaRepository(entityManager);
    }

    public CategoriaEntity criarCategoria(CategoriaEntity categoria) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            if (categoria.getNome() == null || categoria.getNome().isEmpty()) {
                throw new RuntimeException("Nome da categoria é obrigatório!");
            }

            List<CategoriaEntity> todas = listarCategorias();
            boolean jaExiste = todas.stream()
                    .anyMatch(c -> c.getNome().equalsIgnoreCase(categoria.getNome()));

            if (jaExiste) {
                throw new RuntimeException("A categoria '" + categoria.getNome() + "' já está cadastrada!");
            }

            if ("PICOLE".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("5.00"));
            } else if ("PESO".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("60.00"));
            } else if ("POTE".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("25.00"));
            } else {
                throw new RuntimeException("Categoria inválida! Use PICOLE, PESO ou POTE.");
            }

            categoriaRepository.salvar(categoria);
            transaction.commit();
            return categoria;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public CategoriaEntity buscarPorId(Long id) {
        return categoriaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));
    }

    public List<CategoriaEntity> listarCategorias() {
        return categoriaRepository.listarTodos();
    }

    public CategoriaEntity atualizarCategoria(Long id, CategoriaEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            CategoriaEntity categoria = categoriaRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            categoria.setNome(dadosAtualizados.getNome());
            categoria.setDescricao(dadosAtualizados.getDescricao());

            if ("PICOLE".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("5.00"));
            } else if ("PESO".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("60.00"));
            } else if ("POTE".equalsIgnoreCase(categoria.getNome())) {
                categoria.setValor(new BigDecimal("25.00"));
            }

            CategoriaEntity atualizada = categoriaRepository.salvar(categoria);
            transaction.commit();
            return atualizada;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarCategoria(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            CategoriaEntity categoria = categoriaRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            categoriaRepository.deletar(categoria);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}