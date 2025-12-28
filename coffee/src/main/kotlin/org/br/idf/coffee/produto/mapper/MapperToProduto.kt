package org.br.idf.coffee.produto.mapper

import org.br.idf.coffee.categoria.entity.CategoriaEntity
import org.br.idf.coffee.produto.dto.request.ProdutoRequest
import org.br.idf.coffee.produto.dto.response.ProdutoResponse
import org.br.idf.coffee.produto.entity.ProdutoEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Locale

@Component
class MapperToProduto {
    fun fromEntity(produto: ProdutoEntity): ProdutoResponse {
        return ProdutoResponse(
            id = produto.id,
            nome = produto.nome,
            descricao = produto.descricao,
            preco = produto.precoVenda,
            estoque = produto.quantidadeEstoque,
            categoria = produto.categoria.nome,
            precoCusto = produto.precoCusto,
            icone = produto.icone,
            insumos = emptyList()

        )
    }

    fun toEntity(categoria: CategoriaEntity, request: ProdutoRequest) = ProdutoEntity(
        nome = request.nome.uppercase(Locale.getDefault()),
        descricao = request.descricao,
        precoVenda = request.preco,
        custoAquisicao = request.custoAquisicao,
        categoria = categoria,
        quantidadeEstoque = request.estoque,
        ativo = true,
        icone = request.icone,
        precoCusto = BigDecimal.ZERO
    )
}