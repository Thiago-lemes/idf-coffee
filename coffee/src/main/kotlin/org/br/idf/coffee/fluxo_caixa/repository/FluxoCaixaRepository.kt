package org.br.idf.coffee.fluxo_caixa.repository

import org.br.idf.coffee.fluxo_caixa.entity.FluxoCaixaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FluxoCaixaRepository : JpaRepository<FluxoCaixaEntity, Long> {
}