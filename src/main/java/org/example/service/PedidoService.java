package org.example.service;

import jakarta.persistence.*;
import org.example.entity.ClienteEntity;
import org.example.entity.ItemPedidoEntity;
import org.example.entity.PedidoEntity;
import org.example.entity.ProdutoEntity;
import org.example.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoService {

    private final EntityManager entityManager;
    private final EntityManagerFactory emf;
    private final PedidoRepository pedidoRepository;

    public PedidoService(EntityManagerFactory emf) {
        this.emf = emf;
        this.entityManager = emf.createEntityManager();
        this.pedidoRepository = new PedidoRepository(this.entityManager);
    }

    // MÉTODOS PARA RELATÓRIO

    public BigDecimal calcularFaturamentoTotal() {
        EntityManager em = emf.createEntityManager();
        try {
            BigDecimal total = em.createQuery(
                    "SELECT SUM(i.quantidade * i.precoUnitario) FROM ItemPedidoEntity i WHERE i.pedido.status = 'PAGO'",
                    BigDecimal.class).getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    public Long contarVendasRealizadas() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM PedidoEntity p WHERE p.status = 'PAGO'", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Map<String, BigDecimal> getVendasPorCategoria() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Object[]> resultados = em.createQuery(
                            "SELECT i.produto.categoria.nome, SUM(i.quantidade) " +
                                    "FROM ItemPedidoEntity i WHERE i.pedido.status = 'PAGO' " +
                                    "GROUP BY i.produto.categoria.nome", Object[].class)
                    .getResultList();

            Map<String, BigDecimal> mapa = new HashMap<>();
            for (Object[] res : resultados) {
                mapa.put((String) res[0], (BigDecimal) res[1]);
            }
            return mapa;
        } finally {
            em.close();
        }
    }


    // LISTAR POR STATUS
    public List<PedidoEntity> listarPedidosPorStatus(String status) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery("""
                SELECT DISTINCT p FROM PedidoEntity p
                JOIN FETCH p.cliente
                LEFT JOIN FETCH p.itens i
                LEFT JOIN FETCH i.produto
                WHERE p.status = :status
            """, PedidoEntity.class)
                    .setParameter("status", status)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public List<PedidoEntity> listarPedidosPendentes() {
        return listarPedidosPorStatus("ABERTO");
    }

    // CRIAR PEDIDO
    public PedidoEntity criarPedido(Long clienteId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            ClienteEntity cliente = em.find(ClienteEntity.class, clienteId);
            if (cliente == null)
                throw new RuntimeException("Cliente não encontrado!");

            PedidoEntity pedido = new PedidoEntity();
            pedido.setCliente(cliente);
            pedido.setDataHora(LocalDateTime.now());
            pedido.setStatus("ABERTO");

            em.persist(pedido);

            tx.commit();
            return pedido;

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // CALCULAR TOTAL (FINALMENTE CORRIGIDO)
    public BigDecimal calcularTotal(Long pedidoId) {
        EntityManager em = emf.createEntityManager();

        try {
            PedidoEntity pedido = em.createQuery("""
                SELECT p FROM PedidoEntity p
                LEFT JOIN FETCH p.itens i
                LEFT JOIN FETCH i.produto
                WHERE p.id = :id
            """, PedidoEntity.class)
                    .setParameter("id", pedidoId)
                    .getSingleResult();

            if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
                return BigDecimal.ZERO;
            }

            return pedido.getItens().stream()
                    .filter(i -> i.getPrecoUnitario() != null && i.getQuantidade() != null)
                    .map(i -> i.getPrecoUnitario().multiply(i.getQuantidade()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            em.close();
        }
    }

    // CANCELAR PEDIDO
    public void cancelarPedido(Long pedidoId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            PedidoEntity pedido = em.find(PedidoEntity.class, pedidoId);
            if (pedido == null)
                throw new RuntimeException("Pedido não encontrado!");

            if ("PAGO".equals(pedido.getStatus())) {
                throw new RuntimeException("Pedido já pago não pode ser cancelado!");
            }

            pedido.setStatus("CANCELADO");

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // PAGAR PEDIDO
    public void pagarPedido(Long pedidoId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            PedidoEntity pedido = em.find(PedidoEntity.class, pedidoId);

            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado!");
            }

            if (!"ABERTO".equals(pedido.getStatus())) {
                throw new RuntimeException("Pedido não pode ser pago!");
            }

            pedido.setStatus("PAGO");
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // BUSCAR COMPLETO
    public PedidoEntity buscarCompleto(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery("""
                SELECT p FROM PedidoEntity p
                JOIN FETCH p.cliente
                LEFT JOIN FETCH p.itens i
                LEFT JOIN FETCH i.produto
                WHERE p.id = :id
            """, PedidoEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();

        } finally {
            em.close();
        }
    }

    public void adicionarItem(Long pedidoId, Long produtoId, BigDecimal qtd) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            PedidoEntity pedido = em.find(PedidoEntity.class, pedidoId);
            ProdutoEntity produto = em.find(ProdutoEntity.class, produtoId);

            if (pedido == null || produto == null) {
                throw new RuntimeException("Pedido ou Produto não encontrado!");
            }

            ItemPedidoEntity item = new ItemPedidoEntity();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(qtd);
            item.setPrecoUnitario(produto.getPreco());
            em.persist(item);

            pedido.getItens().add(item);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}