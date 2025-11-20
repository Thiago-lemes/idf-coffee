package org.br.idf.coffee.produto.service

import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.br.idf.coffee.produto.dto.ProdutoRequestDTO
import org.br.idf.coffee.produto.dto.ProdutoResponseDTO
import org.br.idf.coffee.produto.repository.ProdutoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.Locale

@Service
@Transactional
class ProdutoService(private val repository: ProdutoRepository,
                     private val categoriaRepository: CategoriaRepository) {

    fun registrarProduto(dto: ProdutoRequestDTO): ProdutoResponseDTO {
        validateProdutoForCreate(dto)
        val categoria = categoriaRepository.findById(dto.categoriaId)
            .orElseThrow { NoSuchElementException("Categoria com id=${dto.categoriaId} não encontrada") }
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
        val produtoEntity = repository.findById(id).orElseThrow { NoSuchElementException("Produto com id=$id não encontrado") }
        validateProdutoForUpdate(id, dto)
        var categoriaEntity = categoriaRepository.findById(dto.categoriaId)
            .orElseThrow { NoSuchElementException("Categoria com id=${dto.categoriaId} não encontrada") }

        val updated = produtoEntity.apply {
            this.nome = dto.nome.uppercase(Locale.getDefault())
            this.descricao = dto.descricao
            this.preco = dto.preco
            this.quantidadeEstoque = dto.estoque
            this.categoria = categoriaEntity
        }

        val saved = repository.saveAndFlush(updated)

        return ProdutoResponseDTO.fromEntity(saved)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Produto com id=$id não encontrado")
        }
        repository.deleteById(id)
        repository.flush();
    }

    private fun validateProdutoForCreate(dto: ProdutoRequestDTO) {
        validateCommonFields(dto)
        // Se já existir produto com mesmo nome, lança erro
        val nomeNorm = dto.nome.uppercase(Locale.getDefault())
        require(!repository.findByNome(nomeNorm).isPresent) { "Produto já cadastrado: ${dto.nome}" }
    }

    private fun validateProdutoForUpdate(id: Long, dto: ProdutoRequestDTO) {
        validateCommonFields(dto)
        val nomeNorm = dto.nome.uppercase(Locale.getDefault())
        val byName = repository.findByNome(nomeNorm)
        require(!(byName.isPresent && byName.get().id != id)) { "Outro produto com o mesmo nome já existe: ${dto.nome}" }
    }

    private fun validateCommonFields(dto: ProdutoRequestDTO) {
        require(!(dto.nome.isBlank())) { "Nome do produto não pode ser vazio" }
        require(dto.preco >= BigDecimal.ONE) { "Preço do produto deve ser maior que zero" }
    }
}