package org.example.ui.views;

import org.example.entity.CategoriaEntity;
import org.example.entity.ProdutoEntity;
import org.example.service.CategoriaService;
import org.example.service.ProdutoService;
import org.example.ui.Async;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    private JButton criarButton;
    private JButton deletarButton;
    private JButton estoqueAddButton;
    private JButton estoqueRemoveButton;
    private JButton atualizarButton;

    private List<ProdutoEntity> cache;

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
        setLayout(new BorderLayout());
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
                new EmptyBorder(10, 10, 10, 10)
        );

        /*
         * CAMPOS
         */
        nomeField = createField("Nome");
        descricaoField = createField("Descrição");
        precoField = createField("Preço");
        estoqueField = createField("Quantidade");
        produtoIdField = createField("Produto ID");

        /*
         * COMBO CATEGORIA
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

                        if (value instanceof CategoriaEntity categoria) {
                            setText(categoria.getNome());
                        }
                        return this;
                    }
                }
        );

        /*
         * BOTÕES
         */
        criarButton = UI.button("Criar Produto", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });

        deletarButton = UI.button("Deletar Produto", b -> {
            b.setBackground(new Color(200, 60, 60));
            b.setForeground(Color.WHITE);
        });

        estoqueAddButton = UI.button("Adicionar Estoque", b -> {
            b.setBackground(new Color(0, 120, 0));
            b.setForeground(Color.WHITE);
        });

        estoqueRemoveButton = UI.button("Remover Estoque", b -> {
            b.setBackground(new Color(120, 0, 0));
            b.setForeground(Color.WHITE);
        });

        atualizarButton = UI.button("Atualizar Lista", b -> {
            b.setBackground(Theme.PRIMARY_DARK);
            b.setForeground(Color.WHITE);
        });

        /*
         * FORM
         */
        JPanel form = UI.panel(p -> {

                    p.setLayout(
                            new GridLayout(12, 1, 5, 5)
                    );

                    p.setBackground(Theme.SURFACE);

                    p.setBorder(
                            new EmptyBorder(20, 20, 20, 20)
                    );

                    p.setPreferredSize(
                            new Dimension(320, 0)
                    );
                },

                nomeField,
                descricaoField,
                precoField,
                estoqueField,
                categoriaCombo,
                produtoIdField,
                criarButton,
                deletarButton,
                estoqueAddButton,
                estoqueRemoveButton,
                atualizarButton
        );

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

        estoqueAddButton.addActionListener(
                e -> addEstoque()
        );

        estoqueRemoveButton.addActionListener(
                e -> removerEstoque()
        );

        atualizarButton.addActionListener(e -> {
            loadProdutos();
            refreshCategorias();
        });

        listaProdutos.addListSelectionListener(
                e -> preencherCampos()
        );

        /*
         * CLICK FORA
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
                    clearSelection();
                    return;
                }

                if (!SwingUtilities.isDescendingFrom(
                        clicked,
                        listaProdutos
                )) {
                    clearSelection();
                }
            });
        });

        /*
         * KEYBINDS
         */
        Events.keyBinder(this, key -> {
            key.on("ENTER", () -> {
                if (nomeField.isFocusOwner()
                        || descricaoField.isFocusOwner()
                        || precoField.isFocusOwner()
                        || estoqueField.isFocusOwner()) {
                    criarProduto();
                }
            });

            key.on("DELETE", () -> {
                if (listaProdutos.getSelectedIndex() >= 0) {
                    deletarProduto();
                }
            });

            key.on("ESCAPE", this::clearFields);
            key.on("ctrl R", () -> {
                loadProdutos();
                refreshCategorias();
            });
        });
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
        String nome =
                nomeField.getText().trim();

        String descricao =
                descricaoField.getText().trim();

        String preco =
                precoField.getText().trim();

        String estoque =
                estoqueField.getText().trim();

        if (nome.isBlank()
                || preco.isBlank()
                || estoque.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Preencha os campos obrigatórios!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        CategoriaEntity categoria =
                (CategoriaEntity)
                        categoriaCombo.getSelectedItem();

        if (categoria == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma categoria!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Async.compute(
                () -> {
                    ProdutoEntity produto =
                            new ProdutoEntity();

                    produto.setNome(nome);
                    produto.setDescricao(
                            descricao
                    );

                    produto.setPreco(
                            new BigDecimal(preco)
                    );

                    produto.setEstoque(
                            new BigDecimal(estoque)
                    );

                    produtoService.criarProduto(
                            produto,
                            categoria.getId()
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Produto criado com sucesso!"
                    );

                    clearFields();
                    loadProdutos();
                },
                this::showError
        );
    }

    /*
     * DELETAR
     */
    private void deletarProduto() {
        Long id;
        try {
            if (!produtoIdField
                    .getText()
                    .isBlank()) {

                id = Long.parseLong(
                        produtoIdField.getText()
                );

            } else {
                int index =
                        listaProdutos.getSelectedIndex();

                if (index < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Selecione um produto!"
                    );
                    return;
                }
                id = cache.get(index).getId();
            }
        } catch (Exception e) {
            showError(e);
            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja deletar o produto?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Long finalId = id;
        Async.compute(
                () -> {
                    produtoService.deletarProduto(
                            finalId
                    );
                    return null;
                },

                success -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Produto deletado!"
                    );
                    clearFields();
                    loadProdutos();
                },
                this::showError
        );
    }

    /*
     * ADD ESTOQUE
     */
    private void addEstoque() {
        try {
            Long id =
                    Long.parseLong(
                            produtoIdField.getText()
                    );

            BigDecimal qtd =
                    new BigDecimal(
                            estoqueField.getText()
                    );

            Async.compute(
                    () -> {
                        produtoService.adicionarEstoque(
                                id,
                                qtd
                        );
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Estoque adicionado!"
                        );
                        loadProdutos();
                    },
                    this::showError
            );
        } catch (Exception e) {
            showError(e);
        }
    }

    /*
     * REMOVER ESTOQUE
     */
    private void removerEstoque() {
        try {
            Long id =
                    Long.parseLong(
                            produtoIdField.getText()
                    );

            BigDecimal qtd =
                    new BigDecimal(
                            estoqueField.getText()
                    );
            Async.compute(
                    () -> {
                        produtoService.removerEstoque(
                                id,
                                qtd
                        );
                        return null;
                    },

                    success -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Estoque removido!"
                        );
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
                produtoService::listarProdutos,
                produtos -> {
                    cache = produtos;
                    for (ProdutoEntity produto : produtos) {
                        listModel.addElement(
                                "• ID: "
                                        + produto.getId()
                                        + " | "
                                        + produto.getNome()
                                        + " | R$ "
                                        + produto.getPreco()
                                        + " | Estoque: "
                                        + produto.getEstoque().intValue()
                                        + " | Categoria: "
                                        + produto.getCategoria()
                                        .getNome()
                        );
                    }
                },
                this::showError
        );
    }

    /*
     * PREENCHER
     */
    private void preencherCampos() {
        int index =
                listaProdutos.getSelectedIndex();

        if (index < 0 || cache == null) {
            return;
        }

        ProdutoEntity produto =
                cache.get(index);

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

    private void clearSelection() {
        listaProdutos.clearSelection();
    }

    private void clearFields() {
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        produtoIdField.setText("");
        categoriaCombo.setSelectedIndex(-1);
        clearSelection();
    }

    private void showError(Throwable e) {
        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}