package org.example.controller;

import org.example.model.service.PedidoService;
import org.example.view.RelatorioView;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Map;

public class RelatorioController {
    private final RelatorioView view;
    private final PedidoService pedidoService;

    public RelatorioController(
            RelatorioView view,
            PedidoService pedidoService
    ) {
        this.view = view;
        this.pedidoService = pedidoService;
    }

    public void atualizarRelatorio() {
        try {
            BigDecimal faturamento =
                    pedidoService.calcularFaturamentoTotal();
            Long totalPedidos =
                    pedidoService.contarVendasRealizadas();
            Map<String, BigDecimal> vendas =
                    pedidoService.getVendasPorCategoria();

            view.atualizarCards(
                    faturamento,
                    totalPedidos
            );

            view.limparSaidas();
            if (vendas.isEmpty()) {
                view.adicionarMensagemSemVendas();
                return;
            }

            vendas.forEach((categoria, quantidade) -> {
                String nomeExibicao =
                        categoria.equalsIgnoreCase("peso")
                                ? "Buffet"
                                : categoria;

                String sufixo =
                        nomeExibicao.equalsIgnoreCase("Buffet")
                                ? " Kg"
                                : " Unid.";

                String valorFormatado =
                        sufixo.contains("Kg")
                                ? String.format(
                                "%.3f",
                                quantidade
                        )
                                : String.valueOf(
                                quantidade.intValue()
                        );

                view.adicionarItemSaida(
                        nomeExibicao,
                        valorFormatado + sufixo
                );
            });
            view.refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}