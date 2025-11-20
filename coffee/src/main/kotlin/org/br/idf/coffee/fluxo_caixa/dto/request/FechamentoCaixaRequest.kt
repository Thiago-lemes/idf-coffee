package org.br.idf.coffee.fluxo_caixa.dto.request

import java.math.BigDecimal

data class FechamentoCaixaRequest(
    val amount: BigDecimal,
)
