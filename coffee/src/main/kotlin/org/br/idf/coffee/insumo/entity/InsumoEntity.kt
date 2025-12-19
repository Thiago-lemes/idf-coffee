package org.br.idf.coffee.insumo.entity

import jakarta.persistence.*

@Entity
@Table(name = "insumo")
class InsumoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "nome", nullable = false)
    var nome: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false)
    var unidadeMedida: UnidadeMedida,

    @Column(name = "descricao")
    var descricao: String? = null,

    @Column(name = "ativo")
    var ativo: Boolean = true
)

