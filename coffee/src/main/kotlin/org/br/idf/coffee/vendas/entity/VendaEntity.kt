package org.br.idf.coffee.vendas.entity

import jakarta.persistence.*
import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.vendas.entity.enums.FormaDePagamentoEnum
import org.br.idf.coffee.vendas.entity.enums.StatusVendasEnum
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "venda")
data class VendaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: UsuarioEntity,

    @Column(name = "data_venda")
    val dataVenda: LocalDateTime = LocalDateTime.now(),

    @Column(name = "valor_total")
    val valorTotal: BigDecimal,

    @Enumerated(EnumType.STRING)
    val formaPagamento: FormaDePagamentoEnum,

    @Enumerated(EnumType.STRING)
    val status: StatusVendasEnum,

    @OneToMany(mappedBy = "venda", cascade = [CascadeType.ALL], orphanRemoval = true)
    val itens: List<ItemVenda> = mutableListOf(),
)
