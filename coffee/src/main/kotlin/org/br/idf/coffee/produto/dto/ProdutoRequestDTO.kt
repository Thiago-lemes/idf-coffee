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
    var precoCusto: BigDecimal,
    val icone: String? = null
){
    fun toEntity(categoria: CategoriaEntity) = ProdutoEntity(
        nome = nome.uppercase(Locale.getDefault()),
        descricao = descricao,
        precoVenda = preco,
        categoria = categoria,
        quantidadeEstoque = estoque,
        ativo = true,
        icone = icone,
        precoCusto = precoCusto
    )
}
