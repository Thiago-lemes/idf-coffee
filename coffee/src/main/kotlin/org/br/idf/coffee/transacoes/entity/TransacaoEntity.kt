package org.br.idf.coffee.transacoes.entity

import jakarta.persistence.*
import org.br.idf.coffee.fluxo_caixa.entity.CaixaRegistradoraEntity
import org.br.idf.coffee.transacoes.enums.FormaDePagamentoEnum
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transacao")
class TransacaoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_venda")
    var dataVenda: LocalDateTime = LocalDateTime.now(),

    @Column(name = "valor_total", nullable = false)
    var valorTotal: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    var formaPagamento: FormaDePagamentoEnum,

    @ManyToOne
    @JoinColumn(name = "caixa_id")
    val caixa: CaixaRegistradoraEntity,

    @OneToMany(mappedBy = "transacao", cascade = [CascadeType.ALL], orphanRemoval = true)
    var itens: MutableList<TransacaoItemEntity> = mutableListOf()
)
