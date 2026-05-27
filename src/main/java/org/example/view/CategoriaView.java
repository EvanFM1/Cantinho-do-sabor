package org.example.view;

import org.example.controller.CategoriaController;
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
    private final CategoriaController controller;

    private DefaultListModel<String> listModel;
    private JList<String> listaCategorias;

    private JComboBox<String> nomeCombo;
    private JTextField descricaoField;
    private JTextField categoriaIdField;
    private List<CategoriaEntity> cacheCategorias;

    public CategoriaView(CategoriaService service) {
        this.controller =
                new CategoriaController(this, service);
        initComponents();
        controller.carregarCategorias();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);

        // LISTA
        listModel = new DefaultListModel<>();
        listaCategorias =
                new JList<>(listModel);

        listaCategorias.setFont(Theme.TEXT_FONT);
        listaCategorias.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll =
                new JScrollPane(listaCategorias);

        // CAMPOS
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

        // BOTÕES
        PrimaryButton salvarButton =
                new PrimaryButton(
                        "Salvar Categoria"
                );

        JButton deletarButton =
                new JButton(
                        "Deletar Categoria"
                );

        deletarButton.setBackground(
                new Color(200, 50, 50)
        );

        deletarButton.setForeground(
                Color.WHITE
        );

        // FORMULÁRIO
        CardPanel form =
                new CardPanel();

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

        // LAYOUT
        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        // EVENTOS
        salvarButton.addActionListener(
                e -> controller.salvarCategoria()
        );

        deletarButton.addActionListener(
                e -> controller.deletarCategoria()
        );

        // SELEÇÃO
        listaCategorias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });

        // DESELECIONAR AO CLICAR FORA
        Events.mouse(this, mouse -> mouse.onPressed(event -> {
            Component clicked =
                    SwingUtilities.getDeepestComponentAt(
                            CategoriaView.this,
                            event.getX(),
                            event.getY()
                    );

            if (clicked == null ||
                    !SwingUtilities.isDescendingFrom(
                            clicked,
                            listaCategorias
                    )) {
                listaCategorias.clearSelection();
                clearFields();
            }
        }));

        // KEYBINDS
        new KeyBinder(this)
                .on(
                        "ENTER",
                        controller::salvarCategoria
                )

                .on("DELETE", () -> {

                    if (listaCategorias
                            .getSelectedIndex() >= 0) {
                        controller.deletarCategoria();
                    }
                })

                .on(
                        "F5",
                        controller::carregarCategorias
                )

                .on("ESCAPE", () -> {
                    listaCategorias.clearSelection();
                    clearFields();
                });
    }

    public void atualizarLista(
            List<CategoriaEntity> categorias
    ) {

        cacheCategorias = categorias;
        listModel.clear();

        if (categorias == null) {
            return;
        }

        for (CategoriaEntity categoria : categorias) {
            String valor = "";

            if (categoria.getValor() != null &&
                    categoria.getValor().compareTo(
                            java.math.BigDecimal.ZERO
                    ) > 0) {
                valor =
                        " | R$ " + categoria.getValor();
            }

            listModel.addElement(
                    "ID: " + categoria.getId()
                            + " | "
                            + categoria.getNome()
                            + " | "
                            + categoria.getDescricao()
                            + valor
            );
        }
    }

    private void preencherCampos() {
        CategoriaEntity categoria =
                getCategoriaSelecionada();

        if (categoria == null) {
            return;
        }

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

    public void clearFields() {
        descricaoField.setText("");
        categoriaIdField.setText("");
        nomeCombo.setSelectedIndex(0);
        listaCategorias.clearSelection();
    }

    public String getNomeCategoria() {
        return (String)
                nomeCombo.getSelectedItem();
    }

    public String getDescricao() {
        return descricaoField
                .getText()
                .trim();
    }

    public String getCategoriaId() {
        return categoriaIdField
                .getText()
                .trim();
    }

    public CategoriaEntity getCategoriaSelecionada() {
        int index =
                listaCategorias.getSelectedIndex();

        if (index < 0 ||
                cacheCategorias == null) {
            return null;
        }
        return cacheCategorias.get(index);
    }
}