CREATE TABLE IF NOT EXISTS transacao_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(19,2) NOT NULL,
    valor_total DECIMAL(19,2) NOT NULL,

    produto_id BIGINT NOT NULL,
    transacao_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_transacao_item_produto
        FOREIGN KEY (produto_id)
        REFERENCES produto (id),

    CONSTRAINT fk_transacao_item_transacao
        FOREIGN KEY (transacao_id)
        REFERENCES transacao (id)
);
