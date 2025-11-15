package org.br.idf.coffee.transacoes.repository

import org.br.idf.coffee.transacoes.entity.TransacaoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TransacaoRepository : JpaRepository<TransacaoEntity, Long> {
}