package org.example.view;

import org.example.controller.ProdutoController;
import org.example.model.entity.CategoriaEntity;
import org.example.model.entity.ProdutoEntity;
import org.example.model.service.CategoriaService;
import org.example.model.service.ProdutoService;
import org.example.ui.Events;
import org.example.ui.UI;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ProdutoView extends JPanel {
    private final ProdutoController controller;

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

        this.controller =
                new ProdutoController(
                        this,
                        produtoService,
                        categoriaService
                );

        initComponents();
        controller.carregarProdutos();
        controller.carregarCategorias();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        // LISTA
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

        // CAMPOS
        nomeField = createField("Nome");
        descricaoField =
                createField("Descrição");
        precoField =
                createField("Preço");
        estoqueField =
                createField("Quantidade");
        produtoIdField =
                createField("Produto ID");
        categoriaCombo = new JComboBox<>();
        categoriaCombo.setBorder(
                BorderFactory.createTitledBorder(
                        "Categoria"
                )
        );

        categoriaCombo.setBackground(
                Color.WHITE
        );

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
                            setText(
                                    categoria.getNome()
                            );
                        }
                        return this;
                    }
                }
        );

        // BOTÕES
        criarButton =
                UI.button(
                        "Criar Produto",
                        b -> {
                            b.setBackground(
                                    Theme.PRIMARY
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        deletarButton =
                UI.button(
                        "Deletar Produto",
                        b -> {
                            b.setBackground(
                                    new Color(200, 60, 60)
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        estoqueAddButton =
                UI.button(
                        "Adicionar Estoque",
                        b -> {
                            b.setBackground(
                                    new Color(0, 120, 0)
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        estoqueRemoveButton =
                UI.button(
                        "Remover Estoque",
                        b -> {
                            b.setBackground(
                                    new Color(120, 0, 0)
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        atualizarButton =
                UI.button(
                        "Atualizar Lista",
                        b -> {
                            b.setBackground(
                                    Theme.PRIMARY_DARK
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        // FORMULÁRIO
        JPanel form =
                UI.panel(
                        p -> {
                            p.setLayout(
                                    new GridLayout(
                                            12,
                                            1,
                                            5,
                                            5
                                    )
                            );

                            p.setBackground(
                                    Theme.SURFACE
                            );

                            p.setBorder(
                                    new EmptyBorder(
                                            20,
                                            20,
                                            20,
                                            20
                                    )
                            );

                            p.setPreferredSize(
                                    new Dimension(
                                            320,
                                            0
                                    )
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
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        criarButton.addActionListener(
                e -> controller.criarProduto()
        );

        deletarButton.addActionListener(
                e -> controller.deletarProduto()
        );

        estoqueAddButton.addActionListener(
                e -> controller.adicionarEstoque()
        );

        estoqueRemoveButton.addActionListener(
                e -> controller.removerEstoque()
        );

        atualizarButton.addActionListener(
                e -> {
                    controller.carregarProdutos();
                    controller.carregarCategorias();
                }
        );

        listaProdutos.addListSelectionListener(
                e -> {
                    if (!e.getValueIsAdjusting()) {
                        preencherCampos();
                    }
                }
        );

        // CLICK FORA
        Events.mouse(this, mouse -> mouse.onPressed(event -> {
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
        }));

        // KEYBINDS
        Events.keyBinder(this, key -> {
            key.on("ENTER", () -> {
                if (nomeField.isFocusOwner()
                        || descricaoField.isFocusOwner()
                        || precoField.isFocusOwner()
                        || estoqueField.isFocusOwner()) {
                    controller.criarProduto();
                }
            });

            key.on("DELETE", () -> {
                if (listaProdutos.getSelectedIndex() >= 0) {
                    controller.deletarProduto();
                }
            });

            key.on("ESCAPE", this::clearFields);
            key.on("ctrl R", () -> {
                controller.carregarProdutos();
                controller.carregarCategorias();
            });
        });
    }

    private JTextField createField(String title) {
        JTextField field =
                new JTextField();

        field.setFont(
                Theme.TEXT_FONT
        );

        field.setBorder(
                BorderFactory.createTitledBorder(
                        title
                )
        );
        return field;
    }

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

    public String getNome() {
        return nomeField.getText().trim();
    }

    public String getDescricao() {
        return descricaoField.getText().trim();
    }

    public String getPreco() {
        return precoField.getText().trim();
    }

    public String getEstoque() {
        return estoqueField.getText().trim();
    }

    public String getProdutoId() {
        return produtoIdField.getText().trim();
    }

    public CategoriaEntity getCategoriaSelecionada() {
        return (CategoriaEntity)
                categoriaCombo.getSelectedItem();
    }

    public ProdutoEntity getProdutoSelecionado() {
        int index =
                listaProdutos.getSelectedIndex();

        if (index < 0 || cache == null) {
            return null;
        }
        return cache.get(index);
    }

    public JComboBox<CategoriaEntity> getCategoriaCombo() {
        return categoriaCombo;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    public void setCache(
            List<ProdutoEntity> cache
    ) {
        this.cache = cache;
    }

    public void clearSelection() {
        listaProdutos.clearSelection();
    }

    public void clearFields() {
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        produtoIdField.setText("");
        categoriaCombo.setSelectedIndex(-1);
        clearSelection();
    }
}