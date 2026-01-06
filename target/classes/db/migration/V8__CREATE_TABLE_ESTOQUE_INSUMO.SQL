-- Create table for estoque_insumo
CREATE TABLE IF NOT EXISTS estoque_insumo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  insumo_id BIGINT NOT NULL,
  quantidade_atual DECIMAL(19,6) NOT NULL DEFAULT 0,
  custo_total DECIMAL(19,6) NOT NULL DEFAULT 0,
  custo_unitario DECIMAL(19,6) NOT NULL DEFAULT 0,
  version INT DEFAULT 0,
  CONSTRAINT fk_estoque_insumo FOREIGN KEY (insumo_id) REFERENCES insumo(id)
);

