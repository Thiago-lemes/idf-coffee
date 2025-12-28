package org.br.idf.coffee.fluxo_caixa.dto.request

import java.math.BigDecimal

data class SangriaRequest (
    val valor: BigDecimal, val motivo: String)