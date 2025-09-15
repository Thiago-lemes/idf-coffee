CREATE TABLE IF NOT EXISTS fluxo_de_caixa (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    data_movimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao TEXT,
    CONSTRAINT fk_fluxo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);