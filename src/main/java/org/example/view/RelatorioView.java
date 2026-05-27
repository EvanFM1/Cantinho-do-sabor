package org.example.view;

import org.example.model.service.PedidoService;
import org.example.ui.UI;
import org.example.ui.components.CardPanel;
import org.example.ui.theme.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

public class RelatorioView extends JPanel {
    private final PedidoService pedidoService;
    private JPanel cardsContainer;
    private JPanel saidasGrid;

    public RelatorioView(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        initComponents();
    }

    private void initComponents() {
        // Cabeçalho com Título e Botão Atualizar
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Relatório de Vendas");
        title.setFont(Theme.TITLE_FONT.deriveFont(28f));
        title.setForeground(Theme.PRIMARY);

        JButton btnRefresh = UI.button("Atualizar Dados", b -> {
            b.setBackground(Theme.PRIMARY);
            b.setForeground(Color.WHITE);
        });
        btnRefresh.addActionListener(e -> atualizarRelatorio());

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Container principal para permitir refresh
        cardsContainer = new JPanel(new BorderLayout(0, 30));
        cardsContainer.setOpaque(false);

        add(cardsContainer, BorderLayout.CENTER);

        atualizarRelatorio(); // Primeira carga
    }

    private void atualizarRelatorio() {
        cardsContainer.removeAll();

        // Painel Superior: Faturamento e Pedidos
        JPanel topCards = new JPanel(new GridLayout(1, 2, 25, 0));
        topCards.setOpaque(false);

        BigDecimal faturamento = pedidoService.calcularFaturamentoTotal();
        Long totalVendas = pedidoService.contarVendasRealizadas();

        topCards.add(criarCardDestaque("Faturamento Total", "R$ " + (faturamento != null ? String.format("%.2f", faturamento) : "0,00")));
        topCards.add(criarCardDestaque("Total de Pedidos", String.valueOf(totalVendas)));

        // Painel Central: Detalhamento
        CardPanel details = new CardPanel();
        details.setLayout(new BorderLayout());
        details.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel sub = new JLabel("Resumo de Saídas por Categoria");
        sub.setFont(Theme.TITLE_FONT.deriveFont(20f));
        sub.setForeground(Theme.PRIMARY_DARK);
        details.add(sub, BorderLayout.NORTH);

        saidasGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        saidasGrid.setOpaque(false);

        Map<String, BigDecimal> vendas = pedidoService.getVendasPorCategoria();

        if (vendas.isEmpty()) {
            saidasGrid.add(new JLabel("Nenhuma venda 'PAGA' encontrada no sistema."));
        } else {
            vendas.forEach((cat, qtd) -> {
                String nomeExibicao = cat.equalsIgnoreCase("peso") ? "Buffet" : cat;
                String sufixo = (nomeExibicao.equalsIgnoreCase("Buffet") || nomeExibicao.equalsIgnoreCase("peso")) ? " Kg" : " Unid.";

                String valorFormatado = (sufixo.contains("Kg"))
                        ? String.format("%.3f", qtd)
                        : String.valueOf(qtd.intValue());

                saidasGrid.add(criarItemSaida(nomeExibicao, valorFormatado + sufixo));
            });
        }

        details.add(saidasGrid, BorderLayout.CENTER);
        cardsContainer.add(topCards, BorderLayout.NORTH);
        cardsContainer.add(details, BorderLayout.CENTER);

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private CardPanel criarCardDestaque(String t, String v) {
        CardPanel c = new CardPanel();
        c.setLayout(new GridLayout(2, 1, 0, 10));
        c.setPreferredSize(new Dimension(300, 120));
        JLabel lt = new JLabel(t); lt.setFont(Theme.TEXT_FONT.deriveFont(16f));
        JLabel lv = new JLabel(v); lv.setFont(new Font("sans-serif", Font.BOLD, 32));
        lv.setForeground(Theme.PRIMARY);
        c.add(lt); c.add(lv);
        return c;
    }

    private JPanel criarItemSaida(String nome, String valor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PRIMARY, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        JLabel ln = new JLabel(nome.toUpperCase());
        ln.setFont(Theme.TEXT_FONT.deriveFont(Font.BOLD, 14f));
        ln.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lv = new JLabel(valor);
        lv.setFont(Theme.TITLE_FONT.deriveFont(24f));
        lv.setForeground(Theme.PRIMARY);
        lv.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(ln); p.add(Box.createVerticalStrut(5)); p.add(lv);
        return p;
    }
}