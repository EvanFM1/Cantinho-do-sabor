package org.example.ui.views;

import org.example.entity.CategoriaEntity;
import org.example.entity.ProdutoEntity;
import org.example.service.CategoriaService;
import org.example.service.ProdutoService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.bindings.key.KeyBinder;
import org.example.ui.components.CardPanel;
import org.example.ui.components.PrimaryButton;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoView extends JPanel {
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    private DefaultListModel<String> listModel;
    private JList<String> listaProdutos;

    private JTextField nomeField;
    private JTextField descricaoField;
    private JTextField precoField;
    private JTextField estoqueField;
    private JTextField produtoIdField;

    private JComboBox<CategoriaEntity> categoriaCombo;

    private List<ProdutoEntity> cacheProdutos;

    public ProdutoView(
            ProdutoService produtoService,
            CategoriaService categoriaService
    ) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;

        initComponents();
        loadProdutos();
        refreshCategorias();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);

        /*
         * LISTA
         */
        listModel = new DefaultListModel<>();
        listaProdutos = new JList<>(listModel);
        listaProdutos.setFont(Theme.TEXT_FONT);
        listaProdutos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(listaProdutos);

        scroll.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        /*
         * CAMPOS
         */
        nomeField =
                createField("Nome do Produto");

        descricaoField =
                createField("Descrição");

        precoField =
                createField("Preço (R$)");

        estoqueField =
                createField("Estoque");

        produtoIdField =
                createField("ID do Produto");

        /*
         * COMBOBOX
         */
        categoriaCombo = new JComboBox<>();
        categoriaCombo.setBorder(
                BorderFactory.createTitledBorder(
                        "Categoria"
                )
        );

        categoriaCombo.setBackground(Color.WHITE);
        categoriaCombo.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );

                        if (value instanceof CategoriaEntity cat) {
                            setText(cat.getNome());
                        }
                        return this;
                    }
                }
        );

        /*
         * BOTÕES
         */
        PrimaryButton criarButton =
                new PrimaryButton("Criar Produto");

        PrimaryButton atualizarButton =
                new PrimaryButton("Atualizar Lista");

        JButton deletarButton =
                new JButton("Deletar Produto");

        deletarButton.setBackground(
                new Color(200, 50, 50)
        );
        deletarButton.setForeground(Color.WHITE);

        /*
         * FORMULÁRIO
         */
        CardPanel form = new CardPanel();

        form.setLayout(
                new GridLayout(10, 1, 5, 5)
        );

        form.setPreferredSize(
                new Dimension(320, 0)
        );

        form.add(nomeField);
        form.add(descricaoField);
        form.add(precoField);
        form.add(estoqueField);
        form.add(categoriaCombo);
        form.add(criarButton);
        form.add(atualizarButton);
        form.add(new JSeparator());
        form.add(produtoIdField);
        form.add(deletarButton);

        /*
         * LAYOUT
         */
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
         * EVENTOS
         */
        criarButton.addActionListener(
                e -> criarProduto()
        );

        deletarButton.addActionListener(
                e -> deletarProduto()
        );

        atualizarButton.addActionListener(e -> {
            loadProdutos();
            refreshCategorias();
        });

        listaProdutos.addListSelectionListener(
                e -> preencherCampos()
        );

        /*
         * DESELECIONAR AO CLICAR FORA
         */
        Events.mouse(this, mouse -> {
            mouse.onPressed(event -> {
                Component clicked =
                        SwingUtilities.getDeepestComponentAt(
                                ProdutoView.this,
                                event.getX(),
                                event.getY()
                        );

                if (clicked == null) {
                    listaProdutos.clearSelection();
                    return;
                }

                if (!SwingUtilities.isDescendingFrom(
                        clicked,
                        listaProdutos
                )) {
                    listaProdutos.clearSelection();
                }
            });
        });

        /*
         * KEYBINDS
         */
        new KeyBinder(this)
                .on("ENTER", this::criarProduto)
                .on("DELETE", this::deletarProduto)
                .on("F5", () -> {
                    loadProdutos();
                    refreshCategorias();
                })
                .on("ESCAPE", this::clearFields);
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();
        field.setFont(Theme.TEXT_FONT);

        field.setBorder(
                BorderFactory.createTitledBorder(title)
        );
        return field;
    }

    /*
     * CARREGAR CATEGORIAS
     */
    private void refreshCategorias() {
        categoriaCombo.removeAllItems();
        List<CategoriaEntity> categorias =
                categoriaService.listarCategorias();

        for (CategoriaEntity categoria : categorias) {
            categoriaCombo.addItem(categoria);
        }
    }

    /*
     * CRIAR
     */
    private void criarProduto() {
        try {
            ProdutoEntity produto =
                    new ProdutoEntity();

            produto.setNome(
                    nomeField.getText().trim()
            );

            produto.setDescricao(
                    descricaoField.getText().trim()
            );

            produto.setPreco(
                    new BigDecimal(
                            precoField.getText().trim()
                    )
            );

            produto.setEstoque(
                    new BigDecimal(
                            estoqueField.getText().trim()
                    )
            );

            CategoriaEntity categoria =
                    (CategoriaEntity)
                            categoriaCombo.getSelectedItem();

            if (categoria == null) {
                throw new RuntimeException(
                        "Selecione uma categoria!"
                );
            }

            Async.compute(
                    () -> {
                        produtoService.criarProduto(
                                produto,
                                categoria.getId()
                        );
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Produto criado!"
                        );
                        clearFields();
                        loadProdutos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * DELETAR
     */
    private void deletarProduto() {
        try {
            if (produtoIdField.getText().isBlank()) {
                throw new RuntimeException(
                        "Informe o ID do produto!"
                );
            }

            Long id =
                    Long.parseLong(
                            produtoIdField.getText()
                                    .trim()
                    );

            Async.compute(
                    () -> {
                        produtoService.deletarProduto(id);
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Produto removido!"
                        );
                        clearFields();
                        loadProdutos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * LISTAR
     */
    private void loadProdutos() {
        listModel.clear();
        Async.compute(
                () -> produtoService.listarProdutos(),
                (List<ProdutoEntity> produtos) -> {
                    cacheProdutos = produtos;
                    for (ProdutoEntity p : produtos) {
                        listModel.addElement(
                                String.format(
                                        "ID: %d | %s | R$ %.2f | Estoque: %s | Categoria: %s",
                                        p.getId(),
                                        p.getNome(),
                                        p.getPreco(),
                                        p.getEstoque(),
                                        p.getCategoria().getNome()
                                )
                        );
                    }
                },
                this::showError
        );
    }

    /*
     * PREENCHER CAMPOS
     */
    private void preencherCampos() {
        int index =
                listaProdutos.getSelectedIndex();
        if (index < 0
                || cacheProdutos == null) {
            return;
        }

        ProdutoEntity produto =
                cacheProdutos.get(index);

        nomeField.setText(
                produto.getNome()
        );

        descricaoField.setText(
                produto.getDescricao()
        );

        precoField.setText(
                produto.getPreco().toString()
        );

        estoqueField.setText(
                produto.getEstoque().toString()
        );

        produtoIdField.setText(
                produto.getId().toString()
        );

        categoriaCombo.setSelectedItem(
                produto.getCategoria()
        );
    }

    /*
     * LIMPAR
     */
    private void clearFields() {
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        produtoIdField.setText("");
        categoriaCombo.setSelectedIndex(-1);
        listaProdutos.clearSelection();
    }

    /*
     * ERROS
     */
    private void showError(Throwable e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}