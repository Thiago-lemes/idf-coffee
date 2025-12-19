package org.br.idf.coffee.insumo.dto

import org.br.idf.coffee.estoque.entity.EstoqueInsumoEntity
import org.br.idf.coffee.insumo.entity.InsumoEntity
import org.br.idf.coffee.insumo.entity.UnidadeMedida
import java.math.BigDecimal
import java.util.*

data class InsumoRequest(
    val nome: String,
    val unidadeMedida: UnidadeMedida,
    val descricao: String?,
    val custoTotal: BigDecimal,
    val quantidadeTotal: BigDecimal
) {
    fun toEntity() = InsumoEntity(
        nome = nome.uppercase(Locale.getDefault()),
        unidadeMedida = unidadeMedida,
        descricao = descricao
    )

    fun toEstoqueEntity(insumo: InsumoEntity) = EstoqueInsumoEntity(
        insumo = insumo,
        custoTotal = custoTotal,
        quantidadeAtual = quantidadeTotal
    )
}
