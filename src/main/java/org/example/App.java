package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.example.service.CategoriaService;
import org.example.service.ClienteService;
import org.example.service.ItemPedidoService;
import org.example.service.PedidoService;
import org.example.service.ProdutoService;
import org.example.service.UsuarioService;
import org.example.service.VendaService;

import org.example.ui.views.LoginView;

import org.flywaydb.core.Flyway;

import javax.swing.*;

public final class App {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static CategoriaService categoriaService;
    private static ProdutoService produtoService;
    private static PedidoService pedidoService;
    private static VendaService vendaService;
    private static ItemPedidoService itemPedidoService;

    private App() {
    }
    public static void start() {
        inicializarBanco();
        inicializarJPA();
        inicializarServices();

        SwingUtilities.invokeLater(() -> {
            new LoginView(usuarioService,
                    clienteService,
                    pedidoService,
                    produtoService,
                    categoriaService);
        });
    }

    /**
     * Inicializa Flyway e executa migrations.
     */
    private static void inicializarBanco() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/sorveteria",
                        "nick",
                        "nicki12072007"
                )
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
        System.out.println("Iniciando Flyway (Sorveteria)...");

        try {
            flyway.migrate();
            System.out.println("✅ Flyway OK!");
        } catch (Exception exception) {
            System.out.println("⚠️ Aviso Flyway: " + exception.getMessage());
        }
    }

    /**
     * Inicializa JPA/Hibernate.
     */
    private static void inicializarJPA() {
        entityManagerFactory =
                Persistence.createEntityManagerFactory("SorveteriaPU");

        entityManager =
                entityManagerFactory.createEntityManager();
        System.out.println("✅ JPA inicializado!");
    }

    /**
     * Inicializa todos os services.
     */
    private static void inicializarServices() {
        usuarioService =
                new UsuarioService(entityManager);

        clienteService =
                new ClienteService(entityManager);

        categoriaService =
                new CategoriaService(entityManager);

        produtoService =
                new ProdutoService(entityManager);

        pedidoService =
                new PedidoService(entityManager);

        vendaService =
                new VendaService(entityManager);

        itemPedidoService =
                new ItemPedidoService(entityManager);

        System.out.println("✅ Services inicializados!");
    }

    public static void shutdown() {
        try {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
            System.out.println("✅ Recursos encerrados!");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public static ClienteService getClienteService() {
        return clienteService;
    }

    public static CategoriaService getCategoriaService() {
        return categoriaService;
    }

    public static ProdutoService getProdutoService() {
        return produtoService;
    }


    public static PedidoService getPedidoService() {
        return pedidoService;
    }

    public static VendaService getVendaService() {
        return vendaService;
    }

    public static ItemPedidoService getItemPedidoService() {
        return itemPedidoService;
    }
}