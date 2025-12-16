CREATE TABLE IF NOT EXISTS transacao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    caixa_id BIGINT NOT NULL,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    CONSTRAINT fk_transacao_caixa FOREIGN KEY (caixa_id) REFERENCES caixa_registradora(id)
);