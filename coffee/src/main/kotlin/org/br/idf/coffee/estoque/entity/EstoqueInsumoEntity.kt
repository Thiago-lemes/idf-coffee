package org.br.idf.coffee.estoque.entity

import jakarta.persistence.*
import org.br.idf.coffee.insumo.entity.InsumoEntity
import java.math.BigDecimal

@Entity
@Table(name = "estoque_insumo")
class EstoqueInsumoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false, unique = true)
    var insumo: InsumoEntity,

    // Sempre armazenado na unidade base (g, ml ou un)
    @Column(name = "quantidade_atual", nullable = false, precision = 19, scale = 6)
    var quantidadeAtual: BigDecimal = BigDecimal.ZERO,

    @Column(name = "custo_total", nullable = false, precision = 19, scale = 6)
    var custoTotal: BigDecimal = BigDecimal.ZERO,

    // Custo por unidade base (g, ml ou un)
    @Column(name = "custo_unitario", nullable = false, precision = 19, scale = 6)
    var custoUnitario: BigDecimal = BigDecimal.ZERO,
)

