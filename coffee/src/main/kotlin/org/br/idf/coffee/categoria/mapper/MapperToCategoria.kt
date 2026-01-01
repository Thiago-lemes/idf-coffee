package org.br.idf.coffee.categoria.mapper

import org.br.idf.coffee.categoria.dto.CategoriaDTO
import org.br.idf.coffee.categoria.entity.CategoriaEntity
import org.springframework.stereotype.Component

@Component
class MapperToCategoria {
    fun fromEntity(categoria: CategoriaEntity): CategoriaDTO {
        return CategoriaDTO(
            nome = categoria.nome,
            id = categoria.id
        )
    }

    fun toEntity(request: CategoriaDTO) = CategoriaEntity(
        nome = request.nome
    )
}