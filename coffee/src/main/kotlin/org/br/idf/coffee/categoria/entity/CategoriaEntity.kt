package org.br.idf.coffee.categoria.entity

import jakarta.persistence.*

@Entity
@Table(name = "categoria")
data class CategoriaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "nome", nullable = false)
    val nome: String,
    @Column(name = "ativo")
    val ativo: Boolean = true,
)
