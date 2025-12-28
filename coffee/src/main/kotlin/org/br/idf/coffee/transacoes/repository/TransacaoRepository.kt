package org.br.idf.coffee.transacoes.repository

import org.br.idf.coffee.transacoes.entity.TransacaoEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TransacaoRepository : JpaRepository<TransacaoEntity, Long> {
    fun findAllByCaixaId(caixaId: Long, pageable: Pageable): Page<TransacaoEntity>
}