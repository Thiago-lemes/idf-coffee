package org.br.idf.coffee.produto.dto

import org.br.idf.coffee.produto.entity.ProdutoEntity
import org.br.idf.coffee.categoria.entity.CategoriaEntity
import java.math.BigDecimal
import java.util.*

data class ProdutoRequestDTO(
    val nome: String,
    val descricao: String? = null,
    val preco: BigDecimal,
    val categoriaId: Long,
    var estoque: Int = 0,
){
    fun toEntity(categoria: CategoriaEntity) = ProdutoEntity(
        nome = nome.uppercase(Locale.getDefault()),
        descricao = descricao,
        preco = preco,
        categoria = categoria,
        quantidadeEstoque = estoque,
        ativo = true,
    )
}
