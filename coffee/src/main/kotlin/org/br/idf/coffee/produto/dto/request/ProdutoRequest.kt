package org.br.idf.coffee.produto.dto.request

import java.math.BigDecimal

data class ProdutoRequest(
    val nome: String,
    val descricao: String? = null,
    val preco: BigDecimal,
    val categoriaId: Long,
    var estoque: Int = 0,
    var custoAquisicao: BigDecimal,
    val icone: String? = null,
    val insumos: List<ProdutoInsumoRequest> = emptyList()
)