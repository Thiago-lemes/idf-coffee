package org.br.idf.coffee.transacoes.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.br.idf.coffee.produto.entity.ProdutoEntity
import java.math.BigDecimal

@Entity
@Table(name = "transacao_item")
class TransacaoItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "quantidade", nullable = false)
    var quantidade: Int,

    @Column(name = "valor_unitario", nullable = false)
    var valorUnitario: BigDecimal,

    @Column(name = "valor_total", nullable = false)
    var valorTotal: BigDecimal,

    @ManyToOne @JoinColumn(name = "produto_id")
    var produto: ProdutoEntity,

    @ManyToOne @JoinColumn(name = "transacao_id")
    var transacao: TransacaoEntity? = null
)