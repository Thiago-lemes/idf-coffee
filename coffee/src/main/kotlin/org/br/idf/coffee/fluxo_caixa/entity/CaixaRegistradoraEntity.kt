package org.br.idf.coffee.fluxo_caixa.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.Date

@Entity
@Table(name = "caixa_registradora")
class CaixaRegistradoraEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Temporal(TemporalType.DATE)
    @Column(name = "data_culto", nullable = false)
    var dataCulto: Date = Date(),

    @Column(name = "valor_final_por_culto", nullable = false)
    var valorFinalCulto: BigDecimal = BigDecimal.ZERO,

    @Column(name = "valor_fechamento_caixa", nullable = false)
    var valorFechamentoCaixa: BigDecimal = BigDecimal.ZERO,

    @Column(name = "valor_inicial", nullable = false)
    var valorInicial: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_em_debito", nullable = false)
    var totalDebito: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_em_credito", nullable = false)
    var totalCredito: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_em_pix", nullable = false)
    var totalPix: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_em_dinheiro", nullable = false)
    var totalDinheiro: BigDecimal = BigDecimal.ZERO,

    @Column(name = "sangria", nullable = false)
    var sangria: BigDecimal = BigDecimal.ZERO,

    @Column(name = "descricao_sangria", nullable = false)
    var descricaoSangria: String = "",

    @Column(name = "nome_voluntario", nullable = false)
    var nomeVoluntatio: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "culto", nullable = false)
    var culto: CultoEnum = CultoEnum.OUTROS,

    @Column(name = "caixa_aberto", nullable = false)
    var caixaAberto: Boolean = true
)
