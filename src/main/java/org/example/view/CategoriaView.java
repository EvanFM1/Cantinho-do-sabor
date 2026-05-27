package org.example.view;

import org.example.model.entity.CategoriaEntity;
import org.example.model.service.CategoriaService;
import org.example.ui.Events;
import org.example.ui.bindings.key.KeyBinder;
import org.example.ui.components.CardPanel;
import org.example.ui.components.PrimaryButton;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CategoriaView extends JPanel {
    private final CategoriaService service;

    private DefaultListModel<String> listModel;
    private JList<String> listaCategorias;

    private JComboBox<String> nomeCombo;
    private JTextField descricaoField;
    private JTextField categoriaIdField;

    private List<CategoriaEntity> cacheCategorias;

    public CategoriaView(CategoriaService service) {
        this.service = service;
        initComponents();
        loadCategorias();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);

        /*
        LISTA
         */
        listModel = new DefaultListModel<>();
        listaCategorias = new JList<>(listModel);
        listaCategorias.setFont(Theme.TEXT_FONT);
        listaCategorias.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        JScrollPane scroll =
                new JScrollPane(listaCategorias);

        /*
        CAMPOS
         */
        nomeCombo = new JComboBox<>(
                new String[]{
                        "PICOLE",
                        "PESO",
                        "POTE"
                }
        );

        nomeCombo.setBorder(
                BorderFactory.createTitledBorder(
                        "Tipo da Categoria"
                )
        );

        descricaoField = new JTextField();
        descricaoField.setBorder(
                BorderFactory.createTitledBorder(
                        "Descrição"
                )
        );

        descricaoField.setFont(Theme.TEXT_FONT);
        categoriaIdField = new JTextField();

        categoriaIdField.setBorder(
                BorderFactory.createTitledBorder(
                        "ID da Categoria"
                )
        );
        categoriaIdField.setFont(Theme.TEXT_FONT);

        /*
        BOTÕES
         */
        PrimaryButton salvarButton =
                new PrimaryButton("Salvar Categoria");

        JButton deletarButton =
                new JButton("Deletar Categoria");

        deletarButton.setBackground(
                new Color(200, 50, 50)
        );
        deletarButton.setForeground(Color.WHITE);

        /*
        FORMULÁRIO
         */
        CardPanel form = new CardPanel();
        form.setLayout(
                new GridLayout(7, 1, 5, 5)
        );

        form.setPreferredSize(
                new Dimension(320, 0)
        );

        form.add(
                new JLabel(
                        "Gerenciar Categorias",
                        SwingConstants.CENTER
                )
        );
        form.add(nomeCombo);
        form.add(descricaoField);
        form.add(salvarButton);
        form.add(new JSeparator());
        form.add(categoriaIdField);
        form.add(deletarButton);

        /*
        LAYOUT
         */
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        /*
        EVENTOS
         */
        salvarButton.addActionListener(
                e -> salvar()
        );

        deletarButton.addActionListener(
                e -> deletarCategoria()
        );

        /*
        SELEÇÃO
         */
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        /*
        DESELECIONAR AO CLICAR FORA
         */
        Events.mouse(this, mouse -> {
            mouse.onPressed(event -> {
                Component clicked =
                        SwingUtilities.getDeepestComponentAt(
                                CategoriaView.this,
                                event.getX(),
                                event.getY()
                        );

                if (clicked == null
                        || !SwingUtilities.isDescendingFrom(
                        clicked,
                        listaCategorias
                )) {
                    listaCategorias.clearSelection();
                    clearFields();
                }
            });
        });

        /*
        KEYBINDS
         */
        new KeyBinder(this)
                .on("ENTER", this::salvar)
                .on("DELETE", () -> {
                    if (listaCategorias.getSelectedIndex() >= 0) {
                        deletarCategoria();
                    }
                })
                .on("F5", this::loadCategorias)
                .on("ESCAPE", () -> {
                    listaCategorias.clearSelection();
                    clearFields();
                });
    }

    /*
    SALVAR
     */
    private void salvar() {
        try {
            CategoriaEntity categoria =
                    new CategoriaEntity();

            String nomeCategoria =
                    (String) nomeCombo.getSelectedItem();

            categoria.setNome(nomeCategoria);
            categoria.setDescricao(
                    descricaoField.getText().trim()
            );

             // PREÇO FIXO PARA CATEGORIA PESO
            if ("PESO".equals(nomeCategoria)) {
                categoria.setValor(
                        new java.math.BigDecimal("60.00")
                );

            } else {
                categoria.setValor(
                        java.math.BigDecimal.ZERO
                );
            }

            service.criarCategoria(categoria);
            JOptionPane.showMessageDialog(
                    this,
                    "Categoria criada com sucesso!"
            );
            clearFields();
            loadCategorias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*
    DELETAR
     */
    private void deletarCategoria() {
        try {
            if (categoriaIdField.getText().isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe o ID da categoria!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Long id =
                    Long.parseLong(
                            categoriaIdField
                                    .getText()
                                    .trim()
                    );
            service.deletarCategoria(id);
            JOptionPane.showMessageDialog(
                    this,
                    "Categoria removida!"
            );
            clearFields();
            loadCategorias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*
    LISTAR
     */
    private void loadCategorias() {
        try {
            listModel.clear();
            cacheCategorias = service.listarCategorias();
            if (cacheCategorias == null) return;

            for (CategoriaEntity categoria : cacheCategorias) {
                String valor = "";

                if (categoria.getValor() != null &&
                        categoria.getValor().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    valor = " | R$ " + categoria.getValor();
                }

                listModel.addElement(
                        "ID: " + categoria.getId()
                                + " | " + categoria.getNome()
                                + " | " + categoria.getDescricao()
                                + valor
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar categorias: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /*
    PREENCHER CAMPOS
     */
    private void preencherCampos() {
        int index =
                listaCategorias.getSelectedIndex();

        if (index < 0
                || cacheCategorias == null) {
            return;
        }

        CategoriaEntity categoria =
                cacheCategorias.get(index);

        nomeCombo.setSelectedItem(
                categoria.getNome()
        );

        descricaoField.setText(
                categoria.getDescricao()
        );

        categoriaIdField.setText(
                categoria.getId().toString()
        );
    }

    /*
    LIMPAR
     */
    private void clearFields() {
        descricaoField.setText("");
        categoriaIdField.setText("");
        nomeCombo.setSelectedIndex(0);
    }
}