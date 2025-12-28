package org.br.idf.coffee.insumo.service

import org.br.idf.coffee.estoque.component.EstoqueInsumoFactory
import org.br.idf.coffee.insumo.dto.InsumoRequest
import org.br.idf.coffee.insumo.dto.InsumoResponse
import org.br.idf.coffee.insumo.repository.InsumoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InsumoService(
    private val insumoRepository: InsumoRepository,
    private val estoqueFactory: EstoqueInsumoFactory

) {

    @Transactional
    fun registraInsumo(request: InsumoRequest): InsumoResponse {

        val insumo = request.toEntity()

        insumoRepository.save(insumo)

        val estoque = estoqueFactory.criar(
            insumo = insumo,
            quantidade = request.quantidadeTotal,
            unidade = request.unidadeMedida,
            custoTotal = request.custoTotal
        )

        insumo.estoque = estoque

        return InsumoResponse.fromEntity(insumo)
    }

    fun findAll(): List<InsumoResponse> =
        insumoRepository.findAll()
            .map(InsumoResponse::fromEntity)
}