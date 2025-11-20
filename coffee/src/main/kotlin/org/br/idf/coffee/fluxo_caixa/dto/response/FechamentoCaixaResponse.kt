package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity
import java.math.BigDecimal

data class FechamentoCaixaResponse(
    val amount: BigDecimal,
    val open:  Boolean
) {
    companion object {
        fun fromEntity(caixa: CaixaRegistradoraEntity, valorTotal: BigDecimal): FechamentoCaixaResponse {
            return FechamentoCaixaResponse(
                amount = valorTotal,
                open = caixa.caixaAberto
            )
        }
    }
}
