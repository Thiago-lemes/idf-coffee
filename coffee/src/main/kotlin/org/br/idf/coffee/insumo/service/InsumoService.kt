package org.br.idf.coffee.insumo.service

import org.br.idf.coffee.estoque.entity.EstoqueInsumoEntity
import org.br.idf.coffee.estoque.repository.EstoqueInsumoRepository
import org.br.idf.coffee.insumo.dto.InsumoRequest
import org.br.idf.coffee.insumo.dto.InsumoResponse
import org.br.idf.coffee.insumo.repository.InsumoRepository
import org.br.idf.coffee.ultils.ConversorUnidadeMedida
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class InsumoService(
    private val repository: InsumoRepository,
    private val estoqueInsumoRepository: EstoqueInsumoRepository,
    private val conversorUnidadeMedida: ConversorUnidadeMedida
) {

    fun registraInsumo(request: InsumoRequest): InsumoResponse {
        val insumo = repository.save(request.toEntity())
        val quantidadeBase = conversorUnidadeMedida.converterParaUnidadeBase(
            request.quantidadeTotal,
            request.unidadeMedida
        )

        val custoUnitario = request.custoTotal.divide(
            quantidadeBase,
            6, //ESCALA CUSTO UNITARIO,
            RoundingMode.HALF_UP
        )

        val estoqueInsumo = EstoqueInsumoEntity(
            insumo = insumo,
            quantidadeAtual = quantidadeBase,
            custoTotal = request.custoTotal,
            custoUnitario = custoUnitario
        )

        estoqueInsumoRepository.save(estoqueInsumo)
        val unidadeBase = request.unidadeMedida.unidadeBase()

        return InsumoResponse.fromEntity(
            insumo,
            estoqueInsumo,
            unidadeMedidaPorCustoUnitarios = unidadeBase
        )
    }
}
