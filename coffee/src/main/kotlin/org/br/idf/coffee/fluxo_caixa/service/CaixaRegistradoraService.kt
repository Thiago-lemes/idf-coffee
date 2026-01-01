package org.br.idf.coffee.fluxo_caixa.service

import org.br.idf.coffee.fluxo_caixa.dto.request.AberturaCaixaRequest
import org.br.idf.coffee.fluxo_caixa.dto.request.FechamentoCaixaRequest
import org.br.idf.coffee.fluxo_caixa.dto.request.SangriaRequest
import org.br.idf.coffee.fluxo_caixa.dto.response.FechamentoCaixaResponse
import org.br.idf.coffee.fluxo_caixa.dto.response.OpenCaixaResponse
import org.br.idf.coffee.fluxo_caixa.dto.response.StatusCaixaResponse
import org.br.idf.coffee.fluxo_caixa.dto.response.TransacaoResumoDto
import org.br.idf.coffee.fluxo_caixa.repository.CaixaRegistradoraRepository
import org.br.idf.coffee.transacoes.service.TransacaoService
import org.br.idf.coffee.ultils.CalcValoresCaixa
import org.springframework.stereotype.Service

@Service
class CaixaRegistradoraService(
    private val repository: CaixaRegistradoraRepository,
    private val calcValoresCaixa: CalcValoresCaixa,
    private val transacaoService: TransacaoService,
) {

    fun getStatusCaixa(id: Long): StatusCaixaResponse {
        val caixa = repository.findById(id).orElseThrow()
        val totalVendas = calcValoresCaixa.calculaTotalDeVendas(
            caixa.totalDinheiro,
            caixa.totalCredito,
            caixa.totalPix,
            caixa.totalDebito
        )

        val totalDinheiroNoCaixa = calcValoresCaixa.calculaSaldoDinheiroCaixa(
            caixa.valorInicial,
            caixa.totalDinheiro,
            caixa.sangria
        )

        val ultimas = transacaoService.buscarUltimasTransacoes(id, 3)
            .map { TransacaoResumoDto.fromEntity(it) }

        return StatusCaixaResponse.fromEntity(caixa, totalVendas, totalDinheiroNoCaixa, ultimas)
    }

    fun aberturaDeCaixa(request: AberturaCaixaRequest): OpenCaixaResponse {
        val caixa = repository.save(request.toEntity())
        return OpenCaixaResponse.fromEntity(caixa)
    }

    fun fechamentoDeCaixa(id: Long, request: FechamentoCaixaRequest): FechamentoCaixaResponse {
        val fluxoCaixaEntity = repository.findById(id).orElseThrow()
        if (fluxoCaixaEntity.caixaAberto) {
            val totalFechamento = calcValoresCaixa.calculaFechamentoDeCaixa(
                valorDinheiro = fluxoCaixaEntity.totalDinheiro,
                valorInicial = fluxoCaixaEntity.valorInicial,
                sangria = fluxoCaixaEntity.sangria
            )
            val totalEntradasNoCulto = calcValoresCaixa.calculaTotalDeVendas(
                fluxoCaixaEntity.totalDinheiro,
                fluxoCaixaEntity.totalCredito,
                fluxoCaixaEntity.totalDebito,
                0.toBigDecimal()
            )
            fluxoCaixaEntity.caixaAberto = false
            fluxoCaixaEntity.valorFinalCulto = totalEntradasNoCulto
            fluxoCaixaEntity.valorFechamentoCaixa = totalFechamento
            val saved = repository.saveAndFlush(fluxoCaixaEntity)

            return FechamentoCaixaResponse.fromEntity(saved, totalFechamento)
        }
        throw IllegalArgumentException("Caixa já está fechado.")
    }

    fun sangriaCaixa(id: Long, request: SangriaRequest) {
        val caixa = repository.findById(id).orElseThrow()

        val saldoAtual = calcValoresCaixa.calculaSaldoDinheiroCaixa(
            caixa.valorInicial,
            caixa.totalDinheiro,
            caixa.sangria
        )

        if (request.valor > saldoAtual) {
            throw IllegalArgumentException(
                "Valor da sangria é maior que o saldo disponível no caixa."
            )
        }

        val caixaAtualizado = caixa.apply {
            this.sangria = this.sangria.add(request.valor)
            this.descricaoSangria = request.motivo
        }

        repository.saveAndFlush(caixaAtualizado)
    }
}