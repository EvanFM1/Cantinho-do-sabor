CREATE TABLE usuarios (
  id BIGSERIAL PRIMARY KEY,
  login VARCHAR(50) NOT NULL UNIQUE,
  senha VARCHAR(100) NOT NULL,
  perfil VARCHAR(20) NOT NULL
);

CREATE TABLE categorias (
   id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    descricao VARCHAR(500)
);


CREATE TABLE clientes (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(14) UNIQUE,
  telefone VARCHAR(30)
);

CREATE TABLE funcionarios (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cargo VARCHAR(100) NOT NULL,
  telefone VARCHAR(30) NOT NULL
);

CREATE TABLE pedidos (
  id BIGSERIAL PRIMARY KEY,
  data_hora TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL,
  cliente_id BIGINT NOT NULL,
  funcionario_id BIGINT NOT NULL,
  CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
  CONSTRAINT fk_pedido_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios (id)
);

CREATE TABLE produtos (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  descricao VARCHAR(500),
  preco DECIMAL(10, 2) NOT NULL,
  estoque INT NOT NULL DEFAULT 0,
  fk_categoria_id BIGINT NOT NULL,
  CONSTRAINT fk_produto_categoria FOREIGN KEY (fk_categoria_id) REFERENCES categorias (id)
);

CREATE TABLE vendas (
  id BIGSERIAL PRIMARY KEY,
  valor_total DECIMAL(10, 2) NOT NULL,
  data_venda TIMESTAMP NOT NULL,
  metodo_pagamento VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL,
  fk_pedido_id BIGINT NOT NULL,
  CONSTRAINT fk_venda_pedido FOREIGN KEY (fk_pedido_id) REFERENCES pedidos (id)
);