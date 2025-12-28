package org.br.idf.coffee.produto.dto.response

import java.math.BigDecimal

data class ProdutoInsumoResponse(
    val insumoId: Long,
    val nome: String,
    val quantidadePorProduto: BigDecimal
)
