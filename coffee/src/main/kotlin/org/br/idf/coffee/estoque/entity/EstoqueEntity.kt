package org.br.idf.coffee.estoque.entity

import jakarta.persistence.*
import org.br.idf.coffee.fluxo_caixa.entity.TipoMovimentacaoEnum
import org.br.idf.coffee.produto.entity.ProdutoEntity
import java.time.LocalDateTime

@Entity
@Table(name = "estoque")
data class EstoqueEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    val produto: ProdutoEntity,

    @Column(name = "data_movimento")
    val dataMovimento: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val tipo: TipoMovimentacaoEnum,

    @Column(name = "quantidade")
    val quantidade: Int,

    @Column(name = "observacao")
    val observacao: String? = null,

    @Column(name = "atualizado_em")
    val atualizadoEm: LocalDateTime
)
