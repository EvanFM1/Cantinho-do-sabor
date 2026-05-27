package org.example.model.service;

import jakarta.persistence.*;
import org.example.model.entity.PedidoEntity;
import org.example.model.entity.VendaEntity;
import org.example.model.repository.VendaRepository;
import org.example.model.entity.ItemPedidoEntity;
import org.example.model.entity.ProdutoEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendaService {
    private EntityManager entityManager;
    private VendaRepository vendaRepository;

    public VendaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.vendaRepository = new VendaRepository(entityManager);
    }

    public VendaEntity criarVenda(Long pedidoId, String metodoPagamento) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Busca o pedido garantindo que tragamos os itens (evita o erro de Total 0)
            PedidoEntity pedido = entityManager.find(PedidoEntity.class, pedidoId);

            if (pedido == null) throw new RuntimeException("Pedido não encontrado!");
            if ("CANCELADO".equalsIgnoreCase(pedido.getStatus())) throw new RuntimeException("Pedido está CANCELADO!");
            if ("PAGO".equalsIgnoreCase(pedido.getStatus())) throw new RuntimeException("Pedido já foi pago!");

            if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
                throw new RuntimeException("O pedido não possui itens!");
            }

            // CORREÇÃO: Calcular o valor total automaticamente percorrendo os itens
            BigDecimal totalCalculado = BigDecimal.ZERO;
            for (ItemPedidoEntity item : pedido.getItens()) {
                // Subtotal = Preço Unitário * Quantidade
                BigDecimal subtotal = item.getPrecoUnitario().multiply(item.getQuantidade());
                totalCalculado = totalCalculado.add(subtotal);

                // APROVEITA E JÁ BAIXA O ESTOQUE AQUI (Processo simplificado)
                ProdutoEntity produto = item.getProduto();
                if (produto.getEstoque().compareTo(item.getQuantidade()) < 0) {
                    throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
                }
                produto.setEstoque(produto.getEstoque().subtract(item.getQuantidade()));
            }

            VendaEntity venda = new VendaEntity();
            venda.setPedido(pedido);
            venda.setValorTotal(totalCalculado); // Valor real calculado do banco
            venda.setMetodoPagamento(metodoPagamento);
            venda.setStatus("PAGA"); // Já nasce paga para agilizar o caixa
            venda.setDataVenda(LocalDateTime.now());

            pedido.setStatus("PAGO");

            vendaRepository.salvar(venda);
            transaction.commit();
            return venda;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    // Mantive o finalizarVenda caso você queira um processo separado,
    // mas o criarVenda acima já resolve tudo de uma vez.
    public VendaEntity finalizarVenda(Long vendaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            VendaEntity venda = vendaRepository.buscarPorId(vendaId)
                    .orElseThrow(() -> new RuntimeException("Venda não encontrada!"));

            if ("PAGA".equals(venda.getStatus())) throw new RuntimeException("Venda já finalizada!");

            PedidoEntity pedido = venda.getPedido();

            for (ItemPedidoEntity item : pedido.getItens()) {
                ProdutoEntity produto = item.getProduto();
                if (produto.getEstoque().compareTo(item.getQuantidade()) < 0) {
                    throw new RuntimeException("Estoque insuficiente: " + produto.getNome());
                }
                produto.setEstoque(produto.getEstoque().subtract(item.getQuantidade()));
            }

            venda.setStatus("PAGA");
            venda.getPedido().setStatus("PAGO");

            transaction.commit();
            return venda;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }

    public List<VendaEntity> listarVendas() {
        return vendaRepository.listarTodas();
    }

    public void deletarVenda(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            VendaEntity venda = vendaRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Venda não encontrada!"));
            vendaRepository.deletar(venda);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        }
    }
}