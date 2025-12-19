package org.br.idf.coffee.insumo.dto

import org.br.idf.coffee.estoque.entity.EstoqueInsumoEntity
import org.br.idf.coffee.insumo.entity.InsumoEntity
import org.br.idf.coffee.insumo.entity.UnidadeMedida
import java.math.BigDecimal

data class InsumoResponse(
    val id: Long,
    val nome: String,
    val unidadeMedida: UnidadeMedida,
    val quantidadeEstoque: BigDecimal,
    val custoTotal: BigDecimal,
    val custoUnitarios: BigDecimal,
    val unidadeMedidaPorCustoUnitarios: UnidadeMedida
) {
    companion object {
        fun fromEntity(
            isumoEntity: InsumoEntity,
            estoqueInsumoEntity: EstoqueInsumoEntity,
            unidadeMedidaPorCustoUnitarios: UnidadeMedida
        ): InsumoResponse {
            return InsumoResponse(
                id = isumoEntity.id,
                nome = isumoEntity.nome,
                unidadeMedida = isumoEntity.unidadeMedida,
                quantidadeEstoque = estoqueInsumoEntity.quantidadeAtual,
                custoTotal = estoqueInsumoEntity.custoTotal,
                custoUnitarios = estoqueInsumoEntity.custoUnitario,
                unidadeMedidaPorCustoUnitarios = unidadeMedidaPorCustoUnitarios
            )
        }
    }

}
