package org.br.idf.coffee.insumo.dto

import org.br.idf.coffee.insumo.entity.UnidadeMedida
import java.math.BigDecimal

data class InsumoResponse(
    val id: Long,
    val nome: String,
    val unidadeMedida: UnidadeMedida,
    val quantidadeEstoque: BigDecimal,
    val custoTotal: BigDecimal,
    val custoUnitario: BigDecimal,
    val unidadeCusto: UnidadeMedida,
    val descricao: String?
)

