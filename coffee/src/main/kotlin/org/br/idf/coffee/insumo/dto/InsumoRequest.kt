package org.br.idf.coffee.insumo.dto

import org.br.idf.coffee.insumo.entity.UnidadeMedida
import java.math.BigDecimal

data class InsumoRequest(
    val nome: String,
    val unidadeMedida: UnidadeMedida,
    val descricao: String?,
    val custoTotal: BigDecimal,
    val quantidadeTotal: BigDecimal
)