package org.br.idf.coffee.produto.dto

import org.br.idf.coffee.produto.entity.ProdutoEntity

data class ProdutoResponseDTO(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val preco: String,
    val categoria: String,
    val estoque: Int
){
    companion object {
        fun fromEntity(produto: ProdutoEntity): ProdutoResponseDTO {
            return ProdutoResponseDTO(
                id = produto.id,
                nome = produto.nome,
                descricao = produto.descricao,
                preco = produto.preco.toString(),
                estoque = produto.quantidadeEstoque,
                categoria = produto.categoria.nome
            )
        }
    }
}
