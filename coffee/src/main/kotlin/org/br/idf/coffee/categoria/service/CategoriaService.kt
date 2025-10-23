package org.br.idf.coffee.categoria.service

import org.br.idf.coffee.categoria.dto.CategoriaDTO
import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    private val repository: CategoriaRepository
) {
    fun register(dto: CategoriaDTO): CategoriaDTO {
        findByNome(dto.nome)
        return repository.save(dto.toEntity())
            .let(CategoriaDTO::fromEntity)
    }

    fun findById(id: Long): CategoriaDTO? =
        repository.findById(id)
            .map(CategoriaDTO::fromEntity)
            .orElse(null)

    fun findAll(): List<CategoriaDTO> =
        repository.findAll()
            .map(CategoriaDTO::fromEntity)

    fun delete(id: Long) {
        require(repository.existsById(id)) {
            throw IllegalArgumentException("Categoria com ID $id não encontrada para exclusão.")
        }
        repository.deleteById(id)
    }

    fun update(dto: CategoriaDTO, id: Long): CategoriaDTO? {
        val existingCategoria = repository.findById(id).orElse(null)
        findByNome(dto.nome)
        val updatedCategoria = existingCategoria.copy(
            nome = dto.nome
        )
        val savedCategoria = repository.save(updatedCategoria)
        return CategoriaDTO.fromEntity(savedCategoria)
    }

    private fun findByNome(nome: String) {
        require(!repository.findByNome(nome).isPresent) {
            "já existe categoria com o nome '${nome}'."
        }
    }
}