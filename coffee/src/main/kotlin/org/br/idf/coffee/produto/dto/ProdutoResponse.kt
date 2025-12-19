package org.br.idf.coffee.produto.dto

import org.br.idf.coffee.produto.entity.ProdutoEntity
import java.math.BigDecimal

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val icone: String?,
    val preco: BigDecimal,
    val precoCusto: BigDecimal,
    val categoria: String,
    val estoque: Int
){
    companion object {
        fun fromEntity(produto: ProdutoEntity): ProdutoResponse {
            return ProdutoResponse(
                id = produto.id,
                nome = produto.nome,
                descricao = produto.descricao,
                preco = produto.precoVenda,
                estoque = produto.quantidadeEstoque,
                categoria = produto.categoria.nome,
                precoCusto = produto.precoCusto,
                icone = produto.icone
            )
        }
    }
}
