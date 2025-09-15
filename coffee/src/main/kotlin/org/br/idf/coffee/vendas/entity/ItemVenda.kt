package org.br.idf.coffee.vendas.entity

import jakarta.persistence.*
import org.br.idf.coffee.produto.entity.ProdutoEntity
import java.math.BigDecimal

@Entity
@Table(name = "item_venda")
data class ItemVenda(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    val venda: VendaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    val produto: ProdutoEntity,

    @Column(name = "quantidade")
    val quantidade: Int,

    @Column(name = "preco_unitario")
    val precoUnitario: BigDecimal,

    @Column(name = "subtotal")
    val subtotal: BigDecimal
)
