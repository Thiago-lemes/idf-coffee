package org.br.idf.coffee.rabbit

import java.math.BigDecimal
import java.time.LocalDateTime

data class FechamentoCaixaEvent(
    val caixaId: Long,
    val valorFechamento: BigDecimal,
    val valorFinalCulto: BigDecimal,
    val dataFechamento: LocalDateTime
)
