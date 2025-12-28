package org.br.idf.coffee.insumo.dto

import org.br.idf.coffee.insumo.entity.InsumoEntity
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
) {
    companion object {
        fun fromEntity(insumo: InsumoEntity): InsumoResponse =
            InsumoResponse(
                id = insumo.id,
                nome = insumo.nome,
                unidadeMedida = insumo.unidadeMedida,
                quantidadeEstoque = insumo.estoque?.quantidadeAtual ?: BigDecimal.ZERO,
                custoTotal = insumo.estoque?.custoTotal ?: BigDecimal.ZERO,
                custoUnitario = insumo.estoque?.custoUnitario ?:  BigDecimal.ZERO,
                unidadeCusto = insumo.unidadeMedida.unidadeBase(),
                descricao = insumo.descricao
            )
    }
}

