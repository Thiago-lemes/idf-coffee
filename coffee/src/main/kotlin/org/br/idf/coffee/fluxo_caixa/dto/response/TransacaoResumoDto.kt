package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.transacoes.entity.TransacaoEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransacaoResumoDto(
    val id: Long,
    val time: LocalDateTime,
    val amount: BigDecimal,
    val method: String
) {
    companion object {
        fun fromEntity(entity: TransacaoEntity): TransacaoResumoDto = TransacaoResumoDto(
            id = entity.id,
            time = entity.dataVenda,
            amount = entity.valorTotal,
            method = entity.formaPagamento.name
        )
    }
}

