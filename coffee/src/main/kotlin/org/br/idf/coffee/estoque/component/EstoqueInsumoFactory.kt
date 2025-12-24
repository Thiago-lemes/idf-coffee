package org.br.idf.coffee.estoque.component

import org.br.idf.coffee.estoque.entity.EstoqueInsumoEntity
import org.br.idf.coffee.insumo.entity.InsumoEntity
import org.br.idf.coffee.insumo.entity.UnidadeMedida
import org.br.idf.coffee.ultils.ConversorUnidadeMedida
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class EstoqueInsumoFactory(
    private val conversor: ConversorUnidadeMedida
) {

    fun criar(
        insumo: InsumoEntity,
        quantidade: BigDecimal,
        unidade: UnidadeMedida,
        custoTotal: BigDecimal
    ): EstoqueInsumoEntity {

        require(quantidade > BigDecimal.ZERO)
        require(custoTotal > BigDecimal.ZERO)

        val quantidadeBase = conversor.converterParaUnidadeBase(quantidade, unidade)

        require(quantidadeBase > BigDecimal.ZERO)

        val custoUnitario = custoTotal.divide(
            quantidadeBase,
            6,
            RoundingMode.HALF_UP
        )

        return EstoqueInsumoEntity(
            insumo = insumo,
            quantidadeAtual = quantidadeBase,
            custoTotal = custoTotal,
            custoUnitario = custoUnitario
        )
    }
}
