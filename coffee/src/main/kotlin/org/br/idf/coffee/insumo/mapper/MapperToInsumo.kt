package org.br.idf.coffee.insumo.mapper

import org.br.idf.coffee.insumo.dto.InsumoRequest
import org.br.idf.coffee.insumo.dto.InsumoResponse
import org.br.idf.coffee.insumo.entity.InsumoEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Locale

@Component
class MapperToInsumo {
    fun fromEntity(insumo: InsumoEntity): InsumoResponse =
        InsumoResponse(
            id = insumo.id,
            nome = insumo.nome,
            unidadeMedida = insumo.unidadeMedida,
            quantidadeEstoque = insumo.estoque?.quantidadeAtual ?: BigDecimal.ZERO,
            custoTotal = insumo.estoque?.custoTotal ?: BigDecimal.ZERO,
            custoUnitario = insumo.estoque?.custoUnitario ?: BigDecimal.ZERO,
            unidadeCusto = insumo.unidadeMedida.unidadeBase(),
            descricao = insumo.descricao
        )

    fun toEntity(request: InsumoRequest) = InsumoEntity(
        nome = request.nome.uppercase(Locale.getDefault()),
        unidadeMedida = request.unidadeMedida,
        descricao = request.descricao
    )
}