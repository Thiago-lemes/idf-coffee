package org.br.idf.coffee.produto.entity

import jakarta.persistence.*
import org.br.idf.coffee.insumo.entity.InsumoEntity
import java.math.BigDecimal

@Entity
@Table(name = "produto_insumo")
class ProdutoInsumoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    var produto: ProdutoEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id")
    var insumo: InsumoEntity,

    @Column(name = "quantidade", nullable = false, precision = 19, scale = 6)
    var quantidade: BigDecimal,

    @Column(name = "ativo")
    var ativo: Boolean = true
)