-- Função
CREATE OR REPLACE FUNCTION fn_atualizar_status_pedido()
RETURNS TRIGGER AS $$
BEGIN
UPDATE pedidos
SET status = 'PAGO'
WHERE id = NEW.fk_pedido_id;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER trg_venda_paga_pedido
    AFTER INSERT ON vendas
    FOR EACH ROW
    WHEN (NEW.status = 'PAGA')
    EXECUTE FUNCTION fn_atualizar_status_pedido();


-- Função
CREATE OR REPLACE FUNCTION fn_bloquear_venda_pedido_cancelado()
RETURNS TRIGGER AS $$
DECLARE
status_atual VARCHAR(20);
BEGIN
SELECT status INTO status_atual FROM pedidos WHERE id = NEW.fk_pedido_id;

IF (status_atual = 'CANCELADO') THEN
        RAISE EXCEPTION 'Erro: Não é permitido vender um pedido que já está CANCELADO!';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER trg_validar_venda_cancelada
    BEFORE INSERT ON vendas
    FOR EACH ROW
    EXECUTE FUNCTION fn_bloquear_venda_pedido_cancelado();


-- Função
CREATE OR REPLACE FUNCTION fn_log_alteracao_preco()
RETURNS TRIGGER AS $$
BEGIN
    IF (OLD.valor <> NEW.valor) THEN
        RAISE NOTICE 'ALERTA: O valor da categoria % mudou de % para %',
                     OLD.nome, OLD.valor, NEW.valor;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER trg_auditoria_preco
    AFTER UPDATE ON categorias
    FOR EACH ROW
    EXECUTE FUNCTION fn_log_alteracao_preco();