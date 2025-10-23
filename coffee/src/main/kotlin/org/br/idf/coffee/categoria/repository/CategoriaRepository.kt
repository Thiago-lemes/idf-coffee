package org.br.idf.coffee.categoria.repository

import org.br.idf.coffee.categoria.entity.CategoriaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CategoriaRepository: JpaRepository<CategoriaEntity, Long> {
    fun findByNome(nome: String): Optional<CategoriaEntity>
}