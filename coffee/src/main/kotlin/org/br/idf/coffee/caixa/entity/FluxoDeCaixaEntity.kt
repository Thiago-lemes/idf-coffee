package org.br.idf.coffee.caixa.entity

import jakarta.persistence.*
import org.br.idf.coffee.usuario.entity.UsuarioEntity
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "fluxo_de_caixa")
data class FluxoDeCaixaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    val usuario: UsuarioEntity,

    @Column(name = "data_movimento")
    val dataMovimento: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val tipo: TipoMovimentacaoEnum,

    @Column(name = "valor", nullable = false)
    val valor: BigDecimal,

    @Column(name = "descricao")
    val descricao: String? = null
)
