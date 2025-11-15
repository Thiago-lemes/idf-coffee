CREATE TABLE IF NOT EXISTS produto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    categoria_id BIGINT NOT NULL,
    quantidade_estoque INT DEFAULT 0,
    ativo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_categoria_id FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);