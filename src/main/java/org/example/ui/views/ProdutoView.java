package org.example.ui.views;

import org.example.entity.ProdutoEntity;
import org.example.service.ProdutoService;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoView extends JPanel {

    private final ProdutoService produtoService;

    private DefaultListModel<String> listModel;
    private JList<String> listaProdutos;

    private JTextField nomeField;
    private JTextField descricaoField;
    private JTextField precoField;
    private JTextField estoqueField;
    private JTextField categoriaIdField;
    private JTextField produtoIdField;

    private JButton criarButton;
    private JButton deletarButton;
    private JButton estoqueAddButton;
    private JButton estoqueRemoveButton;
    private JButton atualizarButton;

    private List<ProdutoEntity> cache;

    public ProdutoView(ProdutoService produtoService) {
        this.produtoService = produtoService;
        initComponents();
        loadProdutos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        // LISTA
        listModel = new DefaultListModel<>();
        listaProdutos = new JList<>(listModel);
        listaProdutos.setFont(Theme.TEXT_FONT);

        JScrollPane scroll = new JScrollPane(listaProdutos);

        // CAMPOS
        nomeField = createField("Nome");
        descricaoField = createField("Descrição");
        precoField = createField("Preço");
        estoqueField = createField("Quantidade");
        categoriaIdField = createField("Categoria ID");
        produtoIdField = createField("Produto ID");

        // BOTÕES
        criarButton = UI.button("Criar Produto", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        deletarButton = UI.button("Deletar Produto", b -> {
            b.setBackground(Color.RED);
            b.setForeground(Color.WHITE);
        });

        estoqueAddButton = UI.button("Add Estoque", b -> {
            b.setBackground(new Color(0, 120, 0));
            b.setForeground(Color.WHITE);
        });

        estoqueRemoveButton = UI.button("Remover Estoque", b -> {
            b.setBackground(new Color(120, 0, 0));
            b.setForeground(Color.WHITE);
        });

        atualizarButton = UI.button("Atualizar", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        // FORM
        JPanel form = UI.panel(p -> {
                    p.setLayout(new GridLayout(12, 1, 5, 5));
                    p.setBackground(Theme.SURFACE);
                },
                nomeField,
                descricaoField,
                precoField,
                estoqueField,
                categoriaIdField,
                produtoIdField,
                criarButton,
                deletarButton,
                estoqueAddButton,
                estoqueRemoveButton,
                atualizarButton
        );

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        // EVENTS
        criarButton.addActionListener(e -> criarProduto());
        deletarButton.addActionListener(e -> deletarProduto());
        estoqueAddButton.addActionListener(e -> addEstoque());
        estoqueRemoveButton.addActionListener(e -> removerEstoque());
        atualizarButton.addActionListener(e -> loadProdutos());
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setFont(Theme.TEXT_FONT);
        return field;
    }

    // CREATE
    private void criarProduto() {
        try {
            ProdutoEntity p = new ProdutoEntity();
            p.setNome(nomeField.getText());
            p.setDescricao(descricaoField.getText());
            p.setPreco(new BigDecimal(precoField.getText()));
            p.setEstoque(new BigDecimal(estoqueField.getText()));

            Long categoriaId = Long.parseLong(categoriaIdField.getText());

            produtoService.criarProduto(p, categoriaId);
            JOptionPane.showMessageDialog(this, "Produto criado com sucesso!");
            clearFields();
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Deletar Deletar
     */
    private void deletarProduto() {
        try {
            Long id = Long.parseLong(produtoIdField.getText());
            produtoService.deletarProduto(id);

            JOptionPane.showMessageDialog(this, "Produto deletado!");
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Adicionar Estoque
     */
    private void addEstoque() {
        try {
            Long id = Long.parseLong(produtoIdField.getText());
            BigDecimal qtd = new BigDecimal(estoqueField.getText());

            produtoService.adicionarEstoque(id, qtd);
            JOptionPane.showMessageDialog(this, "Estoque adicionado!");
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Remover Estoque
     */
    private void removerEstoque() {
        try {
            Long id = Long.parseLong(produtoIdField.getText());
            BigDecimal qtd = new BigDecimal(estoqueField.getText());

            produtoService.removerEstoque(id, qtd);
            JOptionPane.showMessageDialog(this, "Estoque removido!");
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
    Listar Produtos
     */
    private void loadProdutos() {
        listModel.clear();
        cache = produtoService.listarProdutos();

        for (ProdutoEntity p : cache) {
            listModel.addElement(
                    "ID: " + p.getId()
                            + " | " + p.getNome()
                            + " | R$ " + p.getPreco()
                            + " | Estoque: " + p.getEstoque()
                            + " | Cat: " + p.getCategoria().getId()
            );
        }
    }

    private void clearFields() {
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        categoriaIdField.setText("");
        produtoIdField.setText("");
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}