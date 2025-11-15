package org.br.idf.coffee.produto.repository

import org.br.idf.coffee.produto.entity.ProdutoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProdutoRepository : JpaRepository<ProdutoEntity, Long> {
    fun findByNome(nome: String): Optional<ProdutoEntity>
}