package org.br.idf.coffee.fluxo_caixa.dto.request

import org.br.idf.coffee.fluxo_caixa.entity.CultoEnum
import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity
import java.math.BigDecimal
import java.util.*

data class AberturaCaixaRequest(
    val amount: BigDecimal,
    val operator: String,
    val period: CultoEnum,
    val open: Boolean
) {
    fun toEntity() = CaixaRegistradoraEntity(
        valorInicial = amount,
        nomeVoluntatio = operator.uppercase(Locale.getDefault()),
        culto = CultoEnum.valueOf(period.name),
        caixaAberto = true,
        valorFinalCulto = 0.0.toBigDecimal(),
        valorFechamentoCaixa = 0.0.toBigDecimal(),
        dataCulto = Date(),
        totalDebito = 0.0.toBigDecimal(),
        totalDinheiro = 0.0.toBigDecimal(),
        totalPix = 0.0.toBigDecimal(),
        totalCredito = 0.0.toBigDecimal(),
        sangria = 0.0.toBigDecimal(),
        descricaoSangria = ""
    )
}