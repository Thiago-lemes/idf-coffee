package org.br.idf.coffee.transacoes.dto

import org.br.idf.coffee.transacoes.enums.FormaDePagamentoEnum
import java.math.BigDecimal

data class TransacaoRquest(
    val amountReceived: BigDecimal,
    val change: Double = 0.0,
    val items: List<ItemDto> = emptyList(),
    val paymentMethod: FormaDePagamentoEnum,
) {
    data class ItemDto(
        val id: Long,
        val price: Double? = null,
        val quantity: Int? = null
    )
}

