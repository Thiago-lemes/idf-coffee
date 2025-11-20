package org.br.idf.coffee.categoria.entity

import jakarta.persistence.*

@Entity
@Table(name = "categoria")
 class CategoriaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "nome", nullable = false)
    var nome: String,
    @Column(name = "ativo")
    var ativo: Boolean = true,
)
