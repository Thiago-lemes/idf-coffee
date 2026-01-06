CREATE TABLE IF NOT EXISTS caixa_registradora(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_culto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_final_por_culto DECIMAL(10,2) NOT NULL,
    saldo_final_conferido_pelo_voluntario DECIMAL(10,2) NOT NULL,
    valor_fechamento_caixa DECIMAL(10,2) NOT NULL,
    valor_inicial DECIMAL(10,2) NOT NULL,
    total_em_debito DECIMAL(10,2) NOT NULL,
    total_em_credito DECIMAL(10,2) NOT NULL,
    total_em_pix DECIMAL(10,2) NOT NULL,
    total_em_dinheiro DECIMAL(10,2) NOT NULL,
    sangria DECIMAL(10,2),
    descricao_sangria VARCHAR(200),
    nome_voluntario VARCHAR(50) NOT NULL,
    culto VARCHAR(15) NOT NULL,
    caixa_aberto BOOLEAN NOT NULL
);