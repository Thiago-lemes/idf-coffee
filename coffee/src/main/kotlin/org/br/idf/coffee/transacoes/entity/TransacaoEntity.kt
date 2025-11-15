package org.br.idf.coffee.transacoes.entity

import jakarta.persistence.*
import org.br.idf.coffee.vendas.entity.enums.FormaDePagamentoEnum
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transacao")
data class TransacaoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_venda")
    val dataVenda: LocalDateTime = LocalDateTime.now(),

    @Column(name = "valor_total", nullable = false)
    val valorTotal: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    val formaPagamento: FormaDePagamentoEnum,

    @OneToMany( mappedBy = "transacao", cascade = [CascadeType.ALL],  orphanRemoval = true)
    val itens: MutableList<TransacaoItemEntity> = mutableListOf()
)
