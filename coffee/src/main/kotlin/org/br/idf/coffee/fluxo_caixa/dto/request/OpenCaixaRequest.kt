package org.br.idf.coffee.fluxo_caixa.dto.request

import org.br.idf.coffee.fluxo_caixa.entity.CultoEnum
import org.br.idf.coffee.fluxo_caixa.entity.FluxoCaixaEntity
import java.math.BigDecimal
import java.util.*

data class OpenCaixaRequest(
    val amount: BigDecimal,
    val operator: String,
    val period: CultoEnum,
    val open: Boolean
) {
    fun toEntity() = FluxoCaixaEntity(
        valorInicial = amount,
        nomeVoluntatio = operator.uppercase(Locale.getDefault()),
        culto = CultoEnum.valueOf(period.name),
        caixaAberto = true,
        valorFinal = 0.0.toBigDecimal(),
        valorFechamento = 0.0.toBigDecimal(),
        data = Date(),
        valorCartao = 0.0.toBigDecimal(),
        valorDinheiro = 0.0.toBigDecimal(),
        sangria = 0.0.toBigDecimal(),
        descricaoSangria = ""
    )
}