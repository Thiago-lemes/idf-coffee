package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity
import java.math.BigDecimal

data class StatusCaixaResponse(
    val valorDinheiro: BigDecimal,
    val debito: BigDecimal,
    val credito: BigDecimal,
    val pix: BigDecimal,
    val valorTotal: BigDecimal,
    val valorInicial: BigDecimal,
    val sangria: BigDecimal,
    val caixaOpen: Boolean,
    val ultimasTransacoes: List<TransacaoResumoDto> = emptyList()
) {
    companion object {
        fun fromEntity(
            caixa: CaixaRegistradoraEntity,
            totalVendas: BigDecimal,
            totalDinheiroNoCaixa: BigDecimal,
            ultimas: List<TransacaoResumoDto> = emptyList()
        ): StatusCaixaResponse {
            return StatusCaixaResponse(
                valorDinheiro = caixa.totalDinheiro,
                debito = caixa.totalDebito,
                valorTotal = totalVendas,
                valorInicial = caixa.valorInicial,
                sangria = caixa.sangria,
                caixaOpen = caixa.caixaAberto,
                ultimasTransacoes = ultimas,
                credito = caixa.totalCredito,
                pix = caixa.totalPix
            )
        }
    }
}
