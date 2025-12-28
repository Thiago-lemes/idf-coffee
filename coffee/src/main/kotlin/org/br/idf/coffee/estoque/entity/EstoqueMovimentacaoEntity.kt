package org.br.idf.coffee.estoque.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "estoque_movimentacao")
class EstoqueMovimentacaoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "estoque_insumo_id", nullable = false)
    var estoqueInsumoId: Long,

    @Column(name = "tipo", nullable = false)
    var tipo: String,

    @Column(name = "quantidade", nullable = false, precision = 19, scale = 6)
    var quantidade: BigDecimal,

    @Column(name = "custo_unitario", precision = 19, scale = 6)
    var custoUnitario: BigDecimal? = null,

    @Column(name = "observacao")
    var observacao: String? = null,

    @Column(name = "data_movimentacao")
    var dataMovimentacao: LocalDateTime = LocalDateTime.now()
)

