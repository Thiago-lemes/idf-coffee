package org.br.idf.coffee.produto.dto.response

import java.math.BigDecimal

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val icone: String?,
    val preco: BigDecimal,
    val precoCusto: BigDecimal,
    val categoria: String,
    val estoque: Int,
    val insumos: List<ProdutoInsumoResponse>
)