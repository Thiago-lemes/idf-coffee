CREATE TABLE IF NOT EXISTS fluxo_de_caixa (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_final DECIMAL(10,2) NOT NULL,
    valor_fechamento DECIMAL(10,2) NOT NULL,
    valor_inicial DECIMAL(10,2) NOT NULL,
    valor_em_cartao DECIMAL(10,2) NOT NULL,
    valor_em_dinheiro DECIMAL(10,2) NOT NULL,
    sangria DECIMAL(10,2),
    descricao_sangria VARCHAR(200),
    nome_voluntario VARCHAR(50) NOT NULL,
    culto VARCHAR(15) NOT NULL,
    caixa_aberto BOOLEAN NOT NULL
);