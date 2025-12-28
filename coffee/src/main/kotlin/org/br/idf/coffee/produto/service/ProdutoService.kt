package org.br.idf.coffee.produto.service

import org.br.idf.coffee.categoria.repository.CategoriaRepository
import org.br.idf.coffee.insumo.repository.InsumoRepository
import org.br.idf.coffee.produto.dto.ProdutoInsumoRequest
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
            .orElseThrow { NoSuchElementException("Categoria ${request.categoriaId} não encontrada") }

        val produto = repository.save(request.toEntity(categoria))

        salvarInsumosDoProduto(produto, request.insumos)

        produto.precoCusto = calcularPrecoCusto(produto, request.insumos)
        repository.save(produto)

        return ProdutoResponse.fromEntity(produto)
    }

    fun update(id: Long, request: ProdutoRequest): ProdutoResponse {
        validateProdutoForUpdate(id, request)

        val produto = repository.findById(id)
            .orElseThrow { NoSuchElementException("Produto $id não encontrado") }

        val categoria = categoriaRepository.findById(request.categoriaId)
            .orElseThrow { NoSuchElementException("Categoria ${request.categoriaId} não encontrada") }

        produto.apply {
            nome = request.nome.uppercase(Locale.getDefault())
            descricao = request.descricao
            precoVenda = request.preco
            custoAquisicao = request.custoAquisicao
            quantidadeEstoque = request.estoque
            this.categoria = categoria
            icone = request.icone
        }

        repository.save(produto)

        produtoInsumoRepository.deleteByProdutoId(produto.id)
        salvarInsumosDoProduto(produto, request.insumos)

        produto.precoCusto = calcularPrecoCusto(produto, request.insumos)
        repository.save(produto)

        return ProdutoResponse.fromEntity(produto)
    }

    fun findAll(): List<ProdutoResponse> =
        repository.findAll()
            .map(ProdutoResponse::fromEntity)

    fun findById(id: Long): ProdutoResponse? =
        repository.findById(id)
            .map(ProdutoResponse::fromEntity)
            .orElse(null)

    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Produto com id=$id não encontrado")
        }
        repository.deleteById(id)
    }

    private fun calcularPrecoCusto(
        produto: ProdutoEntity,
        insumos: List<ProdutoInsumoRequest>
    ): BigDecimal {
        if (insumos.isEmpty()) {
            return produto.custoAquisicao
        }

        return insumos.fold(BigDecimal.ZERO) { total, insumoReq ->
            val insumo = insumoRepository.findById(insumoReq.insumoId)
                .orElseThrow { NoSuchElementException("Insumo ${insumoReq.insumoId} não encontrado") }

            total + (insumo.estoque?.custoUnitario?.multiply(insumoReq.quantidadePorProduto) ?: BigDecimal.ZERO)
        }
    }

    private fun salvarInsumosDoProduto(
        produto: ProdutoEntity,
        insumos: List<ProdutoInsumoRequest>
    ) {
        if (insumos.isEmpty()) return

        validarInsumos(insumos)

        val entidades = insumos.map {
            val insumo = insumoRepository.findById(it.insumoId)
                .orElseThrow { NoSuchElementException("Insumo ${it.insumoId} não encontrado") }

            ProdutoInsumoEntity(
                produto = produto,
                insumo = insumo,
                quantidade = it.quantidadePorProduto
            )
        }

        produtoInsumoRepository.saveAll(entidades)
    }

    private fun validarInsumos(insumos: List<ProdutoInsumoRequest>) {
        val ids = insumos.map { it.insumoId }

        require(ids.size == ids.distinct().size) {
            "Não é permitido repetir o mesmo insumo no produto"
        }

        insumos.forEach {
            require(it.quantidadePorProduto > BigDecimal.ZERO) {
                "Quantidade do insumo deve ser maior que zero"
            }
        }
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