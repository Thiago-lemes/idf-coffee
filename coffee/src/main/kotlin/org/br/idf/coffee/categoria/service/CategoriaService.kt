package org.br.idf.coffee.categoria.service

import org.br.idf.coffee.categoria.dto.CategoriaDTO
import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.br.idf.coffee.produto.repository.ProdutoRepository
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    private val repository: CategoriaRepository,
    private val produtoRepository: ProdutoRepository
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
            "Categoria com ID $id não encontrada para exclusão."
        }

        require(!produtoRepository.existsByCategoriaId(id)) {
            "Categoria não pode ser excluída pois possui produtos vinculados."
        }

        repository.deleteById(id)
    }

    fun update(dto: CategoriaDTO, id: Long): CategoriaDTO? {
        val categoria = repository.findById(id).orElse(null)
        findByNome(dto.nome)
        categoria.nome = dto.nome
        val savedCategoria = repository.saveAndFlush(categoria)
        return CategoriaDTO.fromEntity(savedCategoria)
    }

    private fun findByNome(nome: String) {
        require(!repository.findByNome(nome).isPresent) {
            "já existe categoria com o nome '${nome}'."
        }
    }
}