package org.br.idf.coffee.categoria.dto

import org.br.idf.coffee.categoria.entity.CategoriaEntity

data class CategoriaDTO(
    val id: Long? = null,
    val nome: String,
){
    fun toEntity() = CategoriaEntity(
        nome = nome
    )

    companion object {
        fun fromEntity(categoria: CategoriaEntity): CategoriaDTO {
            return CategoriaDTO(
                nome = categoria.nome,
                id = categoria.id
            )
        }
    }
}

