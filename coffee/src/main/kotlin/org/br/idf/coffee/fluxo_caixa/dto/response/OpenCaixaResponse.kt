package org.br.idf.coffee.fluxo_caixa.dto.response

import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity

data class OpenCaixaResponse(
    val id: Long?,
    val open: Boolean
) {
    companion object {
        fun fromEntity(caixa: CaixaRegistradoraEntity): OpenCaixaResponse {
            return OpenCaixaResponse(
                id = caixa.id,
                open = caixa.caixaAberto
            )
        }
    }
}
