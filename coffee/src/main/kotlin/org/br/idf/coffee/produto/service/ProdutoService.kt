package org.br.idf.coffee.produto.service

import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.br.idf.coffee.produto.dto.ProdutoRequestDTO
import org.br.idf.coffee.produto.dto.ProdutoResponseDTO
import org.br.idf.coffee.produto.repository.ProdutoRepository
import org.springframework.stereotype.Service

@Service
class ProdutoService(private val repository: ProdutoRepository,
                     private val categoriaRepository: CategoriaRepository) {

    fun registrarProduto(dto: ProdutoRequestDTO): ProdutoResponseDTO {
        validateProdutoForCreate(dto)
        val categoria = categoriaRepository.findById(dto.categoriaId).orElseThrow { NoSuchElementException("Categoria com id=${dto.categoriaId} não encontrada") }
        val novoProduto = repository.save(dto.toEntity(categoria))
        return ProdutoResponseDTO.fromEntity(novoProduto)
    }

    fun findAll(): List<ProdutoResponseDTO> =
        repository.findAll()
            .map(ProdutoResponseDTO::fromEntity)

    fun findById(id: Long): ProdutoResponseDTO? =
        repository.findById(id)
            .map(ProdutoResponseDTO::fromEntity)
            .orElse(null)

    fun update(id: Long, dto: ProdutoRequestDTO): ProdutoResponseDTO {
        val existing = repository.findById(id).orElseThrow { NoSuchElementException("Produto com id=$id não encontrado") }
        validateProdutoForUpdate(id, dto)
        val categoria = categoriaRepository.findById(dto.categoriaId).orElseThrow { NoSuchElementException("Categoria com id=${dto.categoriaId} não encontrada") }
        val updated = existing.copy(nome = dto.nome, descricao = dto.descricao, preco = dto.preco, categoria = categoria)
        val saved = repository.save(updated)
        return ProdutoResponseDTO.fromEntity(saved)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Produto com id=$id não encontrado")
        }
        repository.deleteById(id)
    }

    private fun validateProdutoForCreate(dto: ProdutoRequestDTO) {
        validateCommonFields(dto)
        require (repository.findByNome(dto.nome).isPresent) {
            throw IllegalArgumentException("Produto Já cadastrado: ${dto.nome}")
        }
    }

    private fun validateProdutoForUpdate(id: Long, dto: ProdutoRequestDTO) {
        validateCommonFields(dto)
        val byName = repository.findByNome(dto.nome)
        require (byName.isPresent && byName.get().id != id) {
            throw IllegalArgumentException("Outro produto com o mesmo nome já existe: ${dto.nome}")
        }
    }

    private fun validateCommonFields(dto: ProdutoRequestDTO) {
        require (dto.nome.isBlank()) ; throw IllegalArgumentException("Nome do produto não pode ser vazio")
        if (dto.preco < 1.toBigDecimal()) ; throw IllegalArgumentException("Preço do produto deve ser maior que zero")
    }
}