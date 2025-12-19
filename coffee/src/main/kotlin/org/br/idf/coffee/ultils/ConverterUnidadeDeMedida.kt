package org.br.idf.coffee.ultils

import org.br.idf.coffee.insumo.entity.UnidadeMedida
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ConversorUnidadeMedida {

    fun converterParaUnidadeBase(
        quantidade: BigDecimal,
        unidade: UnidadeMedida
    ): BigDecimal =
        when (unidade) {
            UnidadeMedida.KG -> quantidade.multiply(BigDecimal("1000")) // kg → g
            UnidadeMedida.G  -> quantidade
            UnidadeMedida.L  -> quantidade.multiply(BigDecimal("1000")) // l → ml
            UnidadeMedida.ML -> quantidade
            UnidadeMedida.UN -> quantidade
        }
}

