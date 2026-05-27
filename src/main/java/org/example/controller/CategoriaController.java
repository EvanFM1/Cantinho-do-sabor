package org.example.controller;

import org.example.model.entity.CategoriaEntity;
import org.example.model.service.CategoriaService;
import org.example.view.CategoriaView;

import javax.swing.*;
import java.math.BigDecimal;

public class CategoriaController {
    private final CategoriaView view;
    private final CategoriaService service;

    public CategoriaController(
            CategoriaView view,
            CategoriaService service
    ) {
        this.view = view;
        this.service = service;
    }

    public void salvarCategoria() {
        try {
            CategoriaEntity categoria =
                    new CategoriaEntity();

            String nomeCategoria =
                    view.getNomeCategoria();

            categoria.setNome(nomeCategoria);
            categoria.setDescricao(
                    view.getDescricao()
            );

            // PREÇO FIXO PESO
            if ("PESO".equals(nomeCategoria)) {
                categoria.setValor(
                        new BigDecimal("60.00")
                );

            } else {
                categoria.setValor(
                        BigDecimal.ZERO
                );
            }

            service.criarCategoria(categoria);
            JOptionPane.showMessageDialog(
                    view,
                    "Categoria criada com sucesso!"
            );

            view.clearFields();
            carregarCategorias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Erro: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // DELETAR
    public void deletarCategoria() {
        try {
            String idText =
                    view.getCategoriaId();

            if (idText.isBlank()) {
                JOptionPane.showMessageDialog(
                        view,
                        "Informe o ID da categoria!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Long id = Long.parseLong(idText);
            service.deletarCategoria(id);
            JOptionPane.showMessageDialog(
                    view,
                    "Categoria removida!"
            );

            view.clearFields();
            carregarCategorias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Erro: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void carregarCategorias() {
        try {
            view.atualizarLista(
                    service.listarCategorias()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Erro ao carregar categorias: "
                            + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}