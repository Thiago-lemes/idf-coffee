package org.br.idf.coffee.produto.repository

import org.br.idf.coffee.produto.entity.ProdutoInsumoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProdutoInsumoRepository : JpaRepository<ProdutoInsumoEntity, Long> {
}

