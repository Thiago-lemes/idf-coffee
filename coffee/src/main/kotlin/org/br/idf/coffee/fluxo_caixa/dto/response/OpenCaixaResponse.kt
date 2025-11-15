package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.fluxo_caixa.entity.FluxoCaixaEntity

data class OpenCaixaResponse(
    val id: Long,
    val open: Boolean
) {
    companion object {
        fun fromEntity(caixa: FluxoCaixaEntity): OpenCaixaResponse {
            return OpenCaixaResponse(
                id = caixa.id,
                open = caixa.caixaAberto
            )
        }
    }
}
