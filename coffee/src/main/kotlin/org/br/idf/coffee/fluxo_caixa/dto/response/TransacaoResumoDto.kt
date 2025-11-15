package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.transacoes.entity.TransacaoEntity
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransacaoResumoDto(
    val id: Long,
    val dataVenda: LocalDateTime,
    val valorTotal: BigDecimal,
    val formaPagamento: String
) {
    companion object {
        fun fromEntity(entity: TransacaoEntity): TransacaoResumoDto = TransacaoResumoDto(
            id = entity.id,
            dataVenda = entity.dataVenda,
            valorTotal = entity.valorTotal,
            formaPagamento = entity.formaPagamento.name
        )
    }
}

