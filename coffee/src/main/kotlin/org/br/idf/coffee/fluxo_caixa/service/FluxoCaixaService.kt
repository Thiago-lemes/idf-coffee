package org.br.idf.coffee.fluxo_caixa.service

import org.br.idf.coffee.fluxo_caixa.dto.request.OpenCaixaRequest
import org.br.idf.coffee.fluxo_caixa.dto.response.OpenCaixaResponse
import org.br.idf.coffee.fluxo_caixa.dto.response.StatusCaixaResponse
import org.br.idf.coffee.fluxo_caixa.dto.response.TransacaoResumoDto
import org.br.idf.coffee.fluxo_caixa.repository.FluxoCaixaRepository
import org.br.idf.coffee.transacoes.service.TransacaoService
import org.br.idf.coffee.ultils.CalcValoresCaixa
import org.springframework.stereotype.Service

@Service
class FluxoCaixaService(
    private val repository: FluxoCaixaRepository,
    private val calcValoresCaixa: CalcValoresCaixa,
    private val transacaoService: TransacaoService
) {

    fun getStatusCaixa(id: Long): StatusCaixaResponse {
        val caixa = repository.findById(id).orElseThrow()
        val totalVendas = caixa.valorCartao + caixa.valorDinheiro

        val ultimas = transacaoService.buscarUltimasTransacoes(3).map { TransacaoResumoDto.fromEntity(it) }

        return StatusCaixaResponse.fromEntity(caixa, totalVendas, ultimas)
    }


    fun aberturaDeCaixa(dto: OpenCaixaRequest): OpenCaixaResponse {
        val caixa = repository.save(dto.toEntity())
        return OpenCaixaResponse.fromEntity(caixa)
    }
}