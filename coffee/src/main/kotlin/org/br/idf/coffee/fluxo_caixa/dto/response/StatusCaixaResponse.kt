package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.fluxo_caixa.entity.FluxoCaixaEntity
import java.math.BigDecimal

data class StatusCaixaResponse(
    val valorDinheiro: BigDecimal,
    val valorCartao: BigDecimal,
    val valorTotal: BigDecimal,
    val valorInicial: BigDecimal,
    val sangria: BigDecimal,
    val caixaOpen: Boolean,
    val ultimasTransacoes: List<TransacaoResumoDto> = emptyList()
) {
    companion object {
        fun fromEntity(
            caixa: FluxoCaixaEntity,
            total: BigDecimal,
            ultimas: List<TransacaoResumoDto> = emptyList()
        ): StatusCaixaResponse {
            return StatusCaixaResponse(
                valorDinheiro = caixa.valorDinheiro,
                valorCartao = caixa.valorCartao,
                valorTotal = total,
                valorInicial = caixa.valorInicial,
                sangria = caixa.sangria,
                caixaOpen = caixa.caixaAberto,
                ultimasTransacoes = ultimas
            )
        }
    }
}
