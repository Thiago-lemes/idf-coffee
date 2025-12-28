package org.br.idf.coffee.produto.repository

import org.br.idf.coffee.produto.entity.ProdutoInsumoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProdutoInsumoRepository : JpaRepository<ProdutoInsumoEntity, Long> {
    @Modifying
    @Query("delete from ProdutoInsumoEntity pi where pi.produto.id = :produtoId")
    fun deleteByProdutoId(@Param("produtoId") produtoId: Long)
}

