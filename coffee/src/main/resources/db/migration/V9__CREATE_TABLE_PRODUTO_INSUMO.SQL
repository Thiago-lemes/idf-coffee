-- Create table for produto_insumo (recipe)
CREATE TABLE IF NOT EXISTS produto_insumo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  produto_id BIGINT NOT NULL,
  insumo_id BIGINT NOT NULL,
  quantidade DECIMAL(19,6) NOT NULL,
  ativo BOOLEAN DEFAULT TRUE,
  CONSTRAINT fk_produto_insumo_produto FOREIGN KEY (produto_id) REFERENCES produto(id),
  CONSTRAINT fk_produto_insumo_insumo FOREIGN KEY (insumo_id) REFERENCES insumo(id)
);

