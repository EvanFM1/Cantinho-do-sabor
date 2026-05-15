package org.example.ui.views;

import org.example.entity.CategoriaEntity;
import org.example.service.CategoriaService;
import org.example.ui.components.CardPanel;
import org.example.ui.components.PrimaryButton;
import org.example.ui.theme.Theme;
import javax.swing.*;
import java.awt.*;

public class CategoriaView extends JPanel {
    private final CategoriaService service;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JComboBox<String> nomeCombo = new JComboBox<>(new String[]{"PICOLE", "PESO", "POTE"});
    private JTextField descricaoField = new JTextField();

    public CategoriaView(CategoriaService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);
        initComponents();
        loadCategorias();
    }

    private void initComponents() {
        // Lista
        JList<String> lista = new JList<>(listModel);
        lista.setFont(Theme.TEXT_FONT);
        add(new JScrollPane(lista), BorderLayout.CENTER);

        // Formulário
        CardPanel form = new CardPanel();
        form.setLayout(new GridLayout(6, 1, 5, 5));
        form.setPreferredSize(new Dimension(300, 0));

        nomeCombo.setBorder(BorderFactory.createTitledBorder("Tipo de Categoria"));
        descricaoField.setBorder(BorderFactory.createTitledBorder("Descrição (Opcional)"));

        PrimaryButton btn = new PrimaryButton("Salvar Categoria");
        btn.addActionListener(e -> salvar());

        form.add(new JLabel("Nova Categoria", SwingConstants.CENTER));
        form.add(nomeCombo);
        form.add(descricaoField);
        form.add(btn);

        add(form, BorderLayout.EAST);
    }

    private void salvar() {
        try {
            CategoriaEntity cat = new CategoriaEntity();
            cat.setNome((String) nomeCombo.getSelectedItem());
            cat.setDescricao(descricaoField.getText());

            service.criarCategoria(cat);
            JOptionPane.showMessageDialog(this, "Categoria criada com sucesso!");
            loadCategorias();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void loadCategorias() {
        listModel.clear();
        service.listarCategorias().forEach(c ->
                listModel.addElement("ID: " + c.getId() + " | " + c.getNome() + " | Valor: R$ " + c.getValor())
        );
    }
}