package org.br.idf.coffee.fluxo_caixa.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Date

@Entity
@Table(name = "fluxo_de_caixa")
data class FluxoCaixaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "data")
    @Temporal(TemporalType.DATE)
    val data: Date = Date(),

    @Column(name = "valor_final", nullable = false)
    val valorFinal: BigDecimal,

    @Column(name = "valor_fechamento")
    val valorFechamento: BigDecimal,

    @Column(name = "valor_inicial")
    val valorInicial: BigDecimal,

    @Column(name = "valor_em_cartao")
    val valorCartao: BigDecimal,

    @Column(name = "valor_em_dinheiro")
    val valorDinheiro: BigDecimal,

    @Column(name = "sangria")
    val sangria: BigDecimal,

    @Column(name = "descricao_sangria")
    val descricaoSangria: String,

    @Column(name = "nome_voluntario", nullable = false)
    val nomeVoluntatio: String,

    @Column(name = "culto", nullable = false)
    val culto: CultoEnum,

    @Column(name = "caixa_aberto")
    val caixaAberto: Boolean,
)
