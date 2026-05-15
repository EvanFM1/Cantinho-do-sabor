package org.example.ui.views;

import org.example.entity.CategoriaEntity;
import org.example.entity.ProdutoEntity;
import org.example.service.ProdutoService;
import org.example.service.CategoriaService;
import org.example.ui.components.CardPanel;
import org.example.ui.components.PrimaryButton;
import org.example.ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoView extends JPanel {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService; // Adicionado Service de Categoria

    private DefaultListModel<String> listModel;
    private JList<String> listaProdutos;

    private JTextField nomeField;
    private JTextField descricaoField;
    private JTextField precoField;
    private JTextField estoqueField;
    private JTextField produtoIdField;
    private JComboBox<CategoriaEntity> categoriaCombo; // Trocado field por Combo

    public ProdutoView(ProdutoService produtoService, CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        initComponents();
        loadProdutos();
        refreshCategorias(); // Carrega as categorias assim que inicia
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);

        // LISTA (CENTRO)
        listModel = new DefaultListModel<>();
        listaProdutos = new JList<>(listModel);
        listaProdutos.setFont(Theme.TEXT_FONT);
        JScrollPane scroll = new JScrollPane(listaProdutos);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CAMPOS
        nomeField = createField("Nome do Produto");
        descricaoField = createField("Descrição");
        precoField = createField("Preço (R$)");
        estoqueField = createField("Estoque Atual");
        produtoIdField = createField("ID do Produto (p/ Deletar)");

        // COMBOBOX DE CATEGORIAS
        categoriaCombo = new JComboBox<>();
        categoriaCombo.setBorder(BorderFactory.createTitledBorder("Selecione a Categoria"));
        categoriaCombo.setBackground(Color.WHITE);

        // Renderer para mostrar o Nome da Categoria no combo
        categoriaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoriaEntity cat) {
                    setText(cat.getNome());
                }
                return this;
            }
        });

        // BOTÕES USANDO SEUS COMPONENTES
        PrimaryButton criarButton = new PrimaryButton("Criar Produto");
        PrimaryButton atualizarListaButton = new PrimaryButton("Atualizar Lista");

        JButton deletarButton = new JButton("Deletar Produto");
        deletarButton.setBackground(new Color(200, 50, 50));
        deletarButton.setForeground(Color.WHITE);

        // FORMULÁRIO (DIREITA) USANDO CARDPANEL
        CardPanel form = new CardPanel();
        form.setLayout(new GridLayout(9, 1, 5, 5));
        form.setPreferredSize(new Dimension(320, 0));

        form.add(nomeField);
        form.add(descricaoField);
        form.add(precoField);
        form.add(estoqueField);
        form.add(categoriaCombo);
        form.add(criarButton);
        form.add(new JSeparator()); // Linha divisória
        form.add(produtoIdField);
        form.add(deletarButton);

        add(scroll, BorderLayout.CENTER);
        add(form, BorderLayout.EAST);

        // EVENTOS
        criarButton.addActionListener(e -> criarProduto());
        deletarButton.addActionListener(e -> deletarProduto());
        // Botão de atualizar também recarrega o combo de categorias
        atualizarListaButton.addActionListener(e -> {
            loadProdutos();
            refreshCategorias();
        });
    }

    private JTextField createField(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        return field;
    }

    public void refreshCategorias() {
        categoriaCombo.removeAllItems();
        List<CategoriaEntity> categorias = categoriaService.listarCategorias();
        for (CategoriaEntity cat : categorias) {
            categoriaCombo.addItem(cat);
        }
    }

    private void criarProduto() {
        try {
            ProdutoEntity p = new ProdutoEntity();
            p.setNome(nomeField.getText());
            p.setDescricao(descricaoField.getText());
            p.setPreco(new BigDecimal(precoField.getText()));
            p.setEstoque(new BigDecimal(estoqueField.getText()));

            // Pega o objeto categoria selecionado no Combo
            CategoriaEntity selecionada = (CategoriaEntity) categoriaCombo.getSelectedItem();
            if (selecionada == null) throw new RuntimeException("Selecione uma categoria!");

            // Usa o ID da categoria selecionada para salvar
            produtoService.criarProduto(p, selecionada.getId());

            JOptionPane.showMessageDialog(this, "Produto criado com sucesso!");
            clearFields();
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void deletarProduto() {
        try {
            Long id = Long.parseLong(produtoIdField.getText());
            produtoService.deletarProduto(id);
            JOptionPane.showMessageDialog(this, "Produto removido!");
            loadProdutos();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadProdutos() {
        listModel.clear();
        List<ProdutoEntity> cache = produtoService.listarProdutos();

        for (ProdutoEntity p : cache) {
            listModel.addElement(
                    String.format("ID: %d | %s | R$ %.2f | Est: %s | Cat: %s",
                            p.getId(), p.getNome(), p.getPreco(), p.getEstoque(), p.getCategoria().getNome())
            );
        }
    }

    private void clearFields() {
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        estoqueField.setText("");
        produtoIdField.setText("");
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}