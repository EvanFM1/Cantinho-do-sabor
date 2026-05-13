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

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SorveteriaPU");
        EntityManager em = emf.createEntityManager();

        UsuarioService usuarioService = new UsuarioService(em);
        ClienteService clienteService = new ClienteService(em);
        CategoriaService categoriaService = new CategoriaService(em);
        ProdutoService produtoService = new ProdutoService(em);
        FuncionarioService funcionarioService = new FuncionarioService(em);
        PedidoService pedidoService = new PedidoService(em);
        VendaService vendaService = new VendaService(em);
        ItemPedidoService itemPedidoService = new ItemPedidoService(em);

        telaLogin(usuarioService);
        int op;

        do {
            System.out.println("\n===== SISTEMA SORVETERIA =====");
            System.out.println("1 - Clientes");
            System.out.println("2 - Categorias (Picolé, Peso, Pote)");
            System.out.println("3 - Produtos (Sabores)");
            System.out.println("4 - Gerenciar Estoque");
            System.out.println("5 - Funcionários");
            System.out.println("6 - Pedidos (Itens/Abrir/Cancelar)");
            System.out.println("7 - Vendas (Caixa/Pagamento)");
            System.out.println("0 - Sair");

            String input = scanner.nextLine();
            op = input.isEmpty() ? -1 : Integer.parseInt(input);

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
                    case 6 -> menuPedidos(pedidoService, itemPedidoService);
                    case 7 -> menuVendas(vendaService, pedidoService);
                }
            } catch (Exception e) {
                System.out.println("🚫 Erro: " + e.getMessage());
            }
        } while (op != 0);
        em.close();
        emf.close();
    }

    private static void telaLogin(UsuarioService service) {
        while (true) {
            try {
                System.out.println("\n===== LOGIN =====");
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                UsuarioEntity usuario = service.login(login, senha);
                System.out.println("✅ Bem-vindo, " + usuario.getLogin() + " (" + usuario.getPerfil() + ")");
                break;
            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

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
                case 2 -> service.listarClientes().forEach(c -> System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome()));
            }
        } while (op != 0);
    }

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
                case 2 -> service.listarCategorias().forEach(c -> System.out.println("ID: " + c.getId() + " | Tipo: " + c.getNome()));
            }
        } while (op != 0);
    }

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
                    System.out.print("Preço (R$): ");
                    p.setPreco(new BigDecimal(scanner.nextLine().replace(",", ".")));
                    System.out.print("Descrição: ");
                    p.setDescricao(scanner.nextLine());
                    System.out.print("ID da Categoria: ");
                    Long catId = Long.parseLong(scanner.nextLine());
                    service.criarProduto(p, catId);
                    System.out.println("✅ Sabor cadastrado!");
                }
                case 2 -> service.listarProdutos().forEach(p -> System.out.println("[🍧] ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: " + p.getPreco() + " | Estoque: " + p.getEstoque()));
            }
        } while (op != 0);
    }

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
                    System.out.print("ID do produto: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade a adicionar (Ex: 10 ou 0.500): ");
                    BigDecimal qtd = new BigDecimal(scanner.nextLine().replace(",", "."));
                    service.adicionarEstoque(id, qtd);
                    System.out.println("✅ Estoque atualizado!");
                }
                case 2 -> {
                    System.out.print("ID do produto: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade a remover: ");
                    BigDecimal qtd = new BigDecimal(scanner.nextLine().replace(",", "."));
                    service.removerEstoque(id, qtd);
                    System.out.println("✅ Estoque atualizado!");
                }
                case 3 -> service.listarProdutos().forEach(p -> System.out.println("[🍧] ID: " + p.getId() + " | Nome: " + p.getNome() + " | Estoque: " + p.getEstoque()));
            }
        } while (op != 0);
    }

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
                case 2 -> service.listarFuncionarios().forEach(f -> System.out.println("[👤] ID: " + f.getId() + " | Nome: " + f.getNome()));
            }
        } while (op != 0);
    }

    private static void menuPedidos(PedidoService service, ItemPedidoService itemService) {
        int op;
        do {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1 - Abrir Novo Pedido");
            System.out.println("2 - Listar Pedidos Abertos");
            System.out.println("3 - Cancelar Pedido");
            System.out.println("4 - Adicionar Sabor (Item) ao Pedido");
            System.out.println("5 - Ver Itens de um Pedido");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    System.out.print("ID do Cliente: ");
                    Long cliId = Long.parseLong(scanner.nextLine());
                    System.out.print("ID do Funcionário: ");
                    Long funcId = Long.parseLong(scanner.nextLine());
                    service.criarPedido(cliId, funcId);
                    System.out.println("🍦 Pedido aberto!");
                }
                case 2 -> service.listarPedidosPorStatus("ABERTO").forEach(p ->
                        System.out.println("Pedido ID: " + p.getId() + " | Cliente: " + p.getCliente().getNome()));
                case 4 -> {
                    System.out.print("ID do Pedido: ");
                    Long pId = Long.parseLong(scanner.nextLine());
                    System.out.print("ID do Produto: ");
                    Long prodId = Long.parseLong(scanner.nextLine());
                    System.out.print("Quantidade (Ex: 1 ou 0.550): ");
                    BigDecimal qtd = new BigDecimal(scanner.nextLine().replace(",", "."));
                    itemService.adicionarItem(pId, prodId, qtd);
                    System.out.println("✅ Item adicionado!");
                }
                case 5 -> {
                    System.out.print("ID do Pedido: ");
                    Long pId = Long.parseLong(scanner.nextLine());
                    itemService.listarItensPorPedido(pId).forEach(i ->
                            System.out.println("- " + i.getProduto().getNome() + " | Qtd: " + i.getQuantidade()));
                }
            }
        } while (op != 0);
    }

    private static void menuVendas(VendaService service, PedidoService pedidoService) {
        int op;
        do {
            System.out.println("\n--- VENDAS (CAIXA) ---");
            System.out.println("1 - Registrar Venda (Fechar Pedido)");
            System.out.println("2 - Listar Vendas Realizadas");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    try {
                        System.out.print("ID do Pedido para fechar: ");
                        Long pedidoId = Long.parseLong(scanner.nextLine());

                        // MOSTRAR VALOR ANTES DE CONFIRMAR
                        BigDecimal valorTotal = pedidoService.calcularTotal(pedidoId);
                        System.out.printf("\n💰 VALOR TOTAL DO PEDIDO: R$ %.2f\n", valorTotal);

                        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("⚠️ Este pedido está vazio ou o valor é zero!");
                            break;
                        }

                        System.out.print("Confirmar finalização da venda? (S/N): ");
                        if (scanner.nextLine().equalsIgnoreCase("S")) {
                            System.out.print("Método (Pix/Cartão/Dinheiro): ");
                            String metodo = scanner.nextLine();

                            service.criarVenda(pedidoId, metodo);
                            System.out.println("✅ Venda finalizada e estoque atualizado!");
                        } else {
                            System.out.println("❌ Venda cancelada.");
                        }
                    } catch (Exception e) {
                        System.err.println("🚫 Erro ao finalizar venda: " + e.getMessage());
                    }
                }
                case 2 -> service.listarVendas().forEach(v ->
                        System.out.println("Venda ID: " + v.getId() + " | Valor: R$" + v.getValorTotal() + " | Pagamento: " + v.getMetodoPagamento()));
            }
        } while (op != 0);
    }
}