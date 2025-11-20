package org.br.idf.coffee.fluxo_caixa.repository

import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CaixaRegistradoraRepository : JpaRepository<CaixaRegistradoraEntity, Long> {
}