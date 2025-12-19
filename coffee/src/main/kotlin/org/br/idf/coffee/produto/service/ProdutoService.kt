package org.br.idf.coffee.produto.service

import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.br.idf.coffee.insumo.repository.InsumoRepository
import org.br.idf.coffee.produto.dto.ProdutoRequest
import org.br.idf.coffee.produto.dto.ProdutoResponse
import org.br.idf.coffee.produto.entity.ProdutoEntity
import org.br.idf.coffee.produto.entity.ProdutoInsumoEntity
import org.br.idf.coffee.produto.repository.ProdutoInsumoRepository
import org.br.idf.coffee.produto.repository.ProdutoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
@Transactional
class ProdutoService(
    private val repository: ProdutoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val insumoRepository: InsumoRepository,
    private val produtoInsumoRepository: ProdutoInsumoRepository
) {

    fun registrarProduto(request: ProdutoRequest): ProdutoResponse {
        validateProdutoForCreate(request)
        val categoria = categoriaRepository.findById(request.categoriaId)
            .orElseThrow { NoSuchElementException("Categoria com id=${request.categoriaId} não encontrada") }
        val novoProduto = repository.save(request.toEntity(categoria))

        if (request.insumo != null && request.quantiaInsumoPorProduto != null) {
            vinculaProdutoAInsumo(novoProduto, request.insumo, request.quantiaInsumoPorProduto)
        }

        return ProdutoResponse.fromEntity(novoProduto)
    }

    fun findAll(): List<ProdutoResponse> =
        repository.findAll()
            .map(ProdutoResponse::fromEntity)

    fun findById(id: Long): ProdutoResponse? =
        repository.findById(id)
            .map(ProdutoResponse::fromEntity)
            .orElse(null)

    fun update(id: Long, request: ProdutoRequest): ProdutoResponse {
        val produtoEntity =
            repository.findById(id).orElseThrow { NoSuchElementException("Produto com id=$id não encontrado") }
        validateProdutoForUpdate(id, request)
        val categoriaEntity = categoriaRepository.findById(request.categoriaId)
            .orElseThrow { NoSuchElementException("Categoria com id=${request.categoriaId} não encontrada") }
        val updated = produtoEntity.apply {
            this.nome = request.nome.uppercase(Locale.getDefault())
            this.descricao = request.descricao
            this.precoVenda = request.preco
            this.quantidadeEstoque = request.estoque
            this.categoria = categoriaEntity
        }

        val saved = repository.saveAndFlush(updated)

        if (request.insumo != null && request.quantiaInsumoPorProduto != null) {
            vinculaProdutoAInsumo(saved, request.insumo, request.quantiaInsumoPorProduto)
        }

        return ProdutoResponse.fromEntity(saved)
    }

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Produto com id=$id não encontrado")
        }
        repository.deleteById(id)
    }

    private fun vinculaProdutoAInsumo(produto: ProdutoEntity, insumoId: Long, quantidadePorProduto: BigDecimal) {
        val insumo = insumoRepository.findById(insumoId)
            .orElseThrow { NoSuchElementException("insumo não encontrado") }

        val produtoInsumo = ProdutoInsumoEntity(
            produto = produto,
            insumo = insumo,
            quantidade = quantidadePorProduto
        )
        produtoInsumoRepository.save(produtoInsumo)
    }

    private fun validateProdutoForCreate(dto: ProdutoRequest) {
        validateCommonFields(dto)
        val nomeProduto = dto.nome.uppercase(Locale.getDefault())
        require(!repository.findByNome(nomeProduto).isPresent) { "Produto já cadastrado: ${'$'}{dto.nome}" }
    }

    private fun validateProdutoForUpdate(id: Long, dto: ProdutoRequest) {
        validateCommonFields(dto)
        val nomeNorm = dto.nome.uppercase(Locale.getDefault())
        val byName = repository.findByNome(nomeNorm)
        require(!(byName.isPresent && byName.get().id != id)) { "Outro produto com o mesmo nome já existe: ${'$'}{dto.nome}" }
    }

    private fun validateCommonFields(dto: ProdutoRequest) {
        require(!(dto.nome.isBlank())) { "Nome do produto não pode ser vazio" }
        require(dto.preco >= BigDecimal.ONE) { "Preço do produto deve ser maior que zero" }
    }
}