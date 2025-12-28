package org.br.idf.coffee.produto.dto

import java.math.BigDecimal

data class ProdutoInsumoRequest(
    val insumoId: Long,
    val quantidadePorProduto: BigDecimal
)
