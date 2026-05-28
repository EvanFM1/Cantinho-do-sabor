package org.example.view;

import org.example.controller.RelatorioController;
import org.example.model.service.PedidoService;
import org.example.ui.UI;
import org.example.ui.components.CardPanel;
import org.example.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class RelatorioView extends JPanel {
    private final RelatorioController controller;

    private JPanel cardsContainer;
    private JPanel topCards;
    private JPanel saidasGrid;

    public RelatorioView(
            PedidoService pedidoService
    ) {

        this.controller =
                new RelatorioController(
                        this,
                        pedidoService
                );

        setLayout(
                new BorderLayout(20, 20)
        );

        setBackground(
                Theme.BACKGROUND
        );

        setBorder(
                new EmptyBorder(30, 30, 30, 30)
        );

        initComponents();
        controller.atualizarRelatorio();
    }

    private void initComponents() {
        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);
        JLabel title =
                new JLabel(
                        "Relatório de Vendas"
                );

        title.setFont(
                Theme.TITLE_FONT
                        .deriveFont(28f)
        );

        title.setForeground(
                Theme.PRIMARY
        );

        JButton atualizarButton =
                UI.button(
                        "Atualizar Dados",
                        b -> {
                            b.setBackground(
                                    Theme.PRIMARY
                            );

                            b.setForeground(
                                    Color.WHITE
                            );
                        }
                );

        atualizarButton.addActionListener(
                e -> controller.atualizarRelatorio()
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                atualizarButton,
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );

        cardsContainer =
                new JPanel(
                        new BorderLayout(0, 30)
                );

        cardsContainer.setOpaque(false);
        topCards =
                new JPanel(
                        new GridLayout(1, 2, 25, 0)
                );

        topCards.setOpaque(false);
        CardPanel details =
                new CardPanel();
        details.setLayout(
                new BorderLayout()
        );

        details.setBorder(
                new EmptyBorder(25, 25, 25, 25)
        );

        JLabel subTitle =
                new JLabel(
                        "Resumo de Saídas por Categoria"
                );

        subTitle.setFont(
                Theme.TITLE_FONT
                        .deriveFont(20f)
        );

        subTitle.setForeground(
                Theme.PRIMARY_DARK
        );

        details.add(
                subTitle,
                BorderLayout.NORTH
        );

        saidasGrid =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                20,
                                20
                        )
                );

        saidasGrid.setOpaque(false);
        details.add(
                saidasGrid,
                BorderLayout.CENTER
        );

        cardsContainer.add(
                topCards,
                BorderLayout.NORTH
        );

        cardsContainer.add(
                details,
                BorderLayout.CENTER
        );

        add(
                cardsContainer,
                BorderLayout.CENTER
        );
    }

    public void atualizarCards(
            BigDecimal faturamento,
            Long totalPedidos
    ) {

        topCards.removeAll();
        String valor =
                faturamento != null
                        ? String.format(
                        "%.2f",
                        faturamento
                )
                        : "0,00";

        topCards.add(
                criarCardDestaque(
                        "Faturamento Total",
                        "R$ " + valor
                )
        );

        topCards.add(
                criarCardDestaque(
                        "Total de Pedidos",
                        String.valueOf(
                                totalPedidos
                        )
                )
        );
    }

    public void limparSaidas() {
        saidasGrid.removeAll();
    }

    public void adicionarMensagemSemVendas() {
        JLabel label =
                new JLabel(
                        """
                        Nenhuma venda
                        'PAGA'
                        encontrada
                        no sistema.
                        """
                );
        label.setFont(
                Theme.TEXT_FONT
        );
        saidasGrid.add(label);
    }

    public void adicionarItemSaida(
            String nome,
            String valor
    ) {
        saidasGrid.add(
                criarItemSaida(
                        nome,
                        valor
                )
        );
    }

    public void refresh() {
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private CardPanel criarCardDestaque(
            String titulo,
            String valor
    ) {

        CardPanel card =
                new CardPanel();

        card.setLayout(
                new GridLayout(2, 1, 0, 10)
        );

        card.setPreferredSize(
                new Dimension(300, 120)
        );

        JLabel tituloLabel =
                new JLabel(titulo);
        tituloLabel.setFont(
                Theme.TEXT_FONT
                        .deriveFont(16f)
        );

        JLabel valorLabel =
                new JLabel(valor);
        valorLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        32
                )
        );

        valorLabel.setForeground(
                Theme.PRIMARY
        );

        card.add(tituloLabel);
        card.add(valorLabel);
        return card;
    }

    private JPanel criarItemSaida(
            String nome,
            String valor
    ) {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(
                new Color(245, 245, 245)
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Theme.PRIMARY,
                                1,
                                true
                        ),

                        new EmptyBorder(
                                15,
                                20,
                                15,
                                20
                        )
                )
        );

        JLabel nomeLabel =
                new JLabel(
                        nome.toUpperCase()
                );

        nomeLabel.setFont(
                Theme.TEXT_FONT
                        .deriveFont(
                                Font.BOLD,
                                14f
                        )
        );

        nomeLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel valorLabel =
                new JLabel(valor);
        valorLabel.setFont(
                Theme.TITLE_FONT
                        .deriveFont(24f)
        );

        valorLabel.setForeground(
                Theme.PRIMARY
        );

        valorLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panel.add(nomeLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valorLabel);
        return panel;
    }
}