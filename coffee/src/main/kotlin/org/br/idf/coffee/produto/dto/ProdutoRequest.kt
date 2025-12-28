package org.br.idf.coffee.produto.dto

import org.br.idf.coffee.categoria.entity.CategoriaEntity
import org.br.idf.coffee.produto.entity.ProdutoEntity
import java.math.BigDecimal
import java.util.*

data class ProdutoRequest(
    val nome: String,
    val descricao: String? = null,
    val preco: BigDecimal,
    val categoriaId: Long,
    var estoque: Int = 0,
    var custoAquisicao: BigDecimal,
    val icone: String? = null,
    val insumos: List<ProdutoInsumoRequest> = emptyList()
){
    fun toEntity(categoria: CategoriaEntity) = ProdutoEntity(
        nome = nome.uppercase(Locale.getDefault()),
        descricao = descricao,
        precoVenda = preco,
        custoAquisicao = custoAquisicao,
        categoria = categoria,
        quantidadeEstoque = estoque,
        ativo = true,
        icone = icone,
        precoCusto = BigDecimal.ZERO
    )
}
