package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public static EntityManager entityManager;
    public Main() {
        configurarJanela();
        criarComponentes();
    }

    public static void main(String[] args) {
        inicializarBanco();
        inicializarJPA();
        SwingUtilities.invokeLater(() -> {
            Main tela = new Main();
            tela.setVisible(true);
        });
    }

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
        try {
            flyway.migrate();
            System.out.println("✅ Flyway OK!");
        } catch (Exception e) {
            System.out.println("❌ Erro Flyway: " + e.getMessage());
        }
    }

    private static void inicializarJPA() {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("SorveteriaPU");
        entityManager = emf.createEntityManager();
        System.out.println("✅ JPA inicializado!");
    }

    private void configurarJanela() {
        setTitle("Cantinho do Sabor 🍦");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void criarComponentes() {
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout());
        painelPrincipal.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("CANTINHO DO SABOR 🍨");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        painelPrincipal.add(titulo, BorderLayout.NORTH);
        JPanel painelCentro = new JPanel();
        painelCentro.setBackground(Color.WHITE);
        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        add(painelPrincipal);
    }
}