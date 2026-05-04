package org.example;

import jakarta.persistence.*;
import org.example.entity.*;
import org.example.service.*;
import org.flywaydb.core.Flyway;
import java.math.BigDecimal;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Flyway - Ajustado para o banco sorveteria
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/sorveteria", "nick", "nicki12072007")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        System.out.println("Iniciando Flyway (Sorveteria)...");
        try {
            flyway.migrate();
            System.out.println("Flyway OK!");
        } catch (Exception e) {
            System.out.println("⚠️ Aviso Flyway: " + e.getMessage());
        }

        // JPA Ajustado para o persistence unit da sorveteria
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SorveteriaPU");
        EntityManager em = emf.createEntityManager();

        // Services da Sorveteria
        UsuarioService usuarioService = new UsuarioService(em);
        ClienteService clienteService = new ClienteService(em);
        CategoriaService categoriaService = new CategoriaService(em);
        ProdutoService produtoService = new ProdutoService(em);
        FuncionarioService funcionarioService = new FuncionarioService(em);
        PedidoService pedidoService = new PedidoService(em);
        VendaService vendaService = new VendaService(em);

        telaLogin(usuarioService);
        int op;

        do {
            System.out.println("\n===== SISTEMA SORVETERIA =====");
            System.out.println("1 - Clientes");
            System.out.println("2 - Categorias (Picolé, Peso, Pote)");
            System.out.println("3 - Produtos (Sabores)");
            System.out.println("4 - Gerenciar Estoque");
            System.out.println("5 - Funcionários");
            System.out.println("6 - Pedidos (Abrir/Finalizar)");
            System.out.println("7 - Vendas (Caixa/Pagamento)");
            System.out.println("0 - Sair");

            op = Integer.parseInt(scanner.nextLine());

            try {
                switch (op) {
                    case 1 -> menuClientes(clienteService);
                    case 2 -> menuCategorias(categoriaService);
                    case 3 -> menuProdutos(produtoService);
                    case 4 -> menuEstoque(produtoService);
                    case 5 -> {
                        if (!usuarioService.isAdmin()) {
                            System.out.println("🚫 Acesso negado! Apenas ADMIN pode acessar Funcionários.");
                            break;
                        }
                        menuFuncionarios(funcionarioService);
                    }
                    case 6 -> menuPedidos(pedidoService);
                    case 7 -> menuVendas(vendaService);
                }
            } catch (Exception e) {
                System.out.println("🚫 " + e.getMessage());
            }
        } while (op != 0);
        em.close();
        emf.close();
    }

    // -=-=-=-=-=-=- LOGIN -=-=-=-=-=-=-
    private static void telaLogin(UsuarioService service) {
        while (true) {
            try {
                System.out.println("\n===== LOGIN =====");

                System.out.print("Login: ");
                String login = scanner.nextLine();

                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                UsuarioEntity usuario = service.login(login, senha);

                System.out.println("✅ Bem-vindo, " + usuario.getLogin() +
                        " (" + usuario.getPerfil() + ")");
                break;

            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    // -=-=-=-=-=-=- CLIENTES -=-=-=-=-=-=-
    private static void menuClientes(ClienteService service) {
        int op;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    ClienteEntity c = new ClienteEntity();
                    System.out.print("Nome: ");
                    c.setNome(scanner.nextLine());
                    System.out.print("CPF: ");
                    c.setCpf(scanner.nextLine());
                    System.out.print("Telefone: ");
                    c.setTelefone(scanner.nextLine());

                    service.criarCliente(c);
                    System.out.println("✅ Cliente cadastrado!");
                }
                case 2 -> {
                    service.listarClientes().forEach(c ->
                            System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- CATEGORIAS -=-=-=-=-=-=-
    private static void menuCategorias(CategoriaService service) {
        int op;
        do {
            System.out.println("\n--- CATEGORIAS ---");
            System.out.println("1 - Criar (PICOLE, PESO ou POTE)");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    CategoriaEntity cat = new CategoriaEntity();
                    System.out.print("Tipo (PICOLE/PESO/POTE): ");
                    cat.setNome(scanner.nextLine());
                    System.out.print("Descrição: ");
                    cat.setDescricao(scanner.nextLine());

                    service.criarCategoria(cat);
                    System.out.println("✅ Categoria criada!");
                }
                case 2 -> {
                    service.listarCategorias().forEach(c ->
                            System.out.println("ID: " + c.getId() + " | Tipo: " + c.getNome() + " | Valor Base: R$" + c.getValor()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- PRODUTOS -=-=-=-=-=-=-
    private static void menuProdutos(ProdutoService service) {
        int op;
        do {
            System.out.println("\n--- PRODUTOS (SABORES) ---");
            System.out.println("1 - Cadastrar Sabor");
            System.out.println("2 - Listar Todos");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    ProdutoEntity p = new ProdutoEntity();
                    System.out.print("Nome do Sabor: ");
                    p.setNome(scanner.nextLine());
                    System.out.print("Preço: ");
                    p.setPreco(scanner.nextBigDecimal());
                    scanner.nextLine();
                    System.out.print("Descrição: ");
                    p.setDescricao(scanner.nextLine());
                    System.out.print("ID da Categoria: ");
                    Long catId = Long.parseLong(scanner.nextLine());

                    service.criarProduto(p, catId);
                    System.out.println("✅ Sabor cadastrado!");
                }
                case 2 -> {
                    service.listarProdutos().forEach(p ->
                            System.out.println("[\uD83C\uDF67] ID: " + p.getId()
                                    + " | Nome: " + p.getNome()
                                    + " | Preço: " + p.getPreco()
                                    + " | Estoque: " + p.getEstoque())
                    );
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- ESTOQUE -=-=-=-=-=-=-
    private static void menuEstoque(ProdutoService service) {
        int op;
        do {
            System.out.println("\n--- ESTOQUE ---");
            System.out.println("1 - Adicionar estoque");
            System.out.println("2 - Remover estoque");
            System.out.println("3 - Listar produtos");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());
            switch (op) {
                case 1 -> {
                    service.listarProdutos().forEach(p ->
                            System.out.println("[\uD83C\uDF67] ID: " + p.getId()
                                    + " | Nome: " + p.getNome()
                                    + " | Preço: " + p.getPreco()
                                    + " | Estoque: " + p.getEstoque())
                    );

                    System.out.println("ID do produto: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade a adicionar: ");
                    int qtd = Integer.parseInt(scanner.nextLine());
                    service.adicionarEstoque(id, qtd);
                    System.out.println("✅ Estoque atualizado!");
                }
                case 2 -> {
                    service.listarProdutos().forEach(p ->
                            System.out.println("[\uD83C\uDF67] ID: " + p.getId()
                                    + " | Nome: " + p.getNome()
                                    + " | Preço: " + p.getPreco()
                                    + " | Estoque: " + p.getEstoque())
                    );

                    System.out.println("ID do produto: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade a remover: ");
                    int qtd = Integer.parseInt(scanner.nextLine());
                    service.removerEstoque(id, qtd);
                    System.out.println("✅ Estoque atualizado!");
                }
                case 3 -> {
                    service.listarProdutos().forEach(p ->
                            System.out.println("[\uD83C\uDF67] ID: " + p.getId()
                                    + " | Nome: " + p.getNome()
                                    + " | Preço: " + p.getPreco()
                                    + " | Estoque: " + p.getEstoque())
                    );
                }
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- FUNCIONÁRIOS -=-=-=-=-=-=-
    private static void menuFuncionarios(FuncionarioService service) {
        int op;
        do {
            System.out.println("\n--- FUNCIONÁRIOS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    FuncionarioEntity f = new FuncionarioEntity();
                    System.out.print("Nome: ");
                    f.setNome(scanner.nextLine());
                    System.out.print("Cargo: ");
                    f.setCargo(scanner.nextLine());
                    System.out.print("Telefone: ");
                    f.setTelefone(scanner.nextLine());

                    service.criarFuncionario(f);
                    System.out.println("✅ Funcionário cadastrado!");
                }
                case 2 -> {
                    service.listarFuncionarios().forEach(f ->
                            System.out.println("[\uD83D\uDC64] ID: " + f.getId() + " | Nome: " + f.getNome() + " | Cargo: " + f.getCargo()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- PEDIDOS -=-=-=-=-=-=-
    private static void menuPedidos(PedidoService service) {
        int op;
        do {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1 - Abrir Novo Pedido");
            System.out.println("2 - Listar Pedidos Pendentes");
            System.out.println("3 - Cancelar Pedido");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    System.out.print("ID do Cliente: ");
                    Long cliId = Long.parseLong(scanner.nextLine());
                    System.out.print("ID do Funcionário: ");
                    Long funcId = Long.parseLong(scanner.nextLine());

                    service.criarPedido(cliId, funcId);
                    System.out.println("🍦 Pedido aberto com sucesso!");
                }
                case 2 -> {
                    service.listarPedidosPorStatus("PENDENTE").forEach(p ->
                            System.out.println("Pedido ID: " + p.getId() + " | Cliente: " + p.getCliente().getNome()));
                }
                case 3 -> {
                    System.out.print("ID do Pedido: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    service.cancelarPedido(id);
                    System.out.println("🚫 Pedido cancelado.");
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- VENDAS (FINANCEIRO) -=-=-=-=-=-=-
    private static void menuVendas(VendaService service) {
        int op;
        do {
            System.out.println("\n--- VENDAS (CAIXA) ---");
            System.out.println("1 - Registrar Venda (Fechar Pedido)");
            System.out.println("2 - Listar Vendas Realizadas");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    System.out.print("ID do Pedido: ");
                    Long pedidoId = Long.parseLong(scanner.nextLine());
                    System.out.print("Valor Total: ");
                    BigDecimal valor = new BigDecimal(scanner.nextLine());
                    System.out.print("Método (Pix/Cartão/Dinheiro): ");
                    String metodo = scanner.nextLine();

                    VendaEntity v = service.criarVenda(pedidoId, valor, metodo);
                    service.finalizarVenda(v.getId());
                    System.out.println("💰 Venda finalizada e paga!");
                }
                case 2 -> {
                    service.listarVendas().forEach(v ->
                            System.out.println("Venda ID: " + v.getId() + " | Valor: R$" + v.getValorTotal() + " | Status: " + v.getStatus()));
                }
            }
        } while (op != 0);
    }
}