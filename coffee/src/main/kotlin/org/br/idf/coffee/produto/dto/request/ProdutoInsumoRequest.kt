package org.br.idf.coffee.produto.dto.request

import java.math.BigDecimal

data class ProdutoInsumoRequest(
    val insumoId: Long,
    val quantidadePorProduto: BigDecimal
)