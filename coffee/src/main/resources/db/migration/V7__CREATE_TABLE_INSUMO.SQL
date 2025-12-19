-- Create table for insumos
CREATE TABLE IF NOT EXISTS insumo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  unidade_medida VARCHAR(5) NOT NULL,
  descricao TEXT,
  ativo BOOLEAN DEFAULT TRUE
);

