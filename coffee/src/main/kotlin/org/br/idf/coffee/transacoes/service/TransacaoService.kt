package org.br.idf.coffee.transacoes.service

import jakarta.transaction.Transactional
import org.br.idf.coffee.produto.entity.ProdutoEntity
import org.br.idf.coffee.produto.repository.ProdutoRepository
import org.br.idf.coffee.transacoes.dto.TransacaoRquest
import org.br.idf.coffee.transacoes.entity.TransacaoEntity
import org.br.idf.coffee.transacoes.entity.TransacaoItemEntity
import org.br.idf.coffee.transacoes.repository.TransacaoRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransacaoService(
    private val transacaoRepository: TransacaoRepository,
    private val produtoRepository: ProdutoRepository
) {

    @Transactional
    fun criarTransacao(dto: TransacaoRquest): TransacaoEntity {
        require(dto.items.isNotEmpty()) { "Transação deve conter ao menos um item." }

        val produtos = carregarProdutos(dto)
        val itens = montarItens(dto, produtos)
        val valorTotal = calcularValorTotal(itens)
        val transacaoBase = TransacaoEntity(
            valorTotal = valorTotal,
            formaPagamento = dto.paymentMethod,
            itens = mutableListOf()
        )
        val transacaoSalva = transacaoRepository.save(transacaoBase)
        itens.forEach { it.transacao = transacaoSalva }
        val transacaoFinal = transacaoSalva.copy(itens = itens.toMutableList())
        persistirEstoque(produtos.values.toList())
        return transacaoRepository.save(transacaoFinal)
    }

    /**
     * Retorna as últimas transações ordenadas por dataVenda (desc).
     * Por padrão retorna 3 registros, mas é possível passar outro limite.
     */
    fun buscarUltimasTransacoes(limit: Int = 3): List<TransacaoEntity> {
        require(limit > 0) { "Limit deve ser maior que zero." }
        val pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dataVenda"))
        return transacaoRepository.findAll(pageRequest).content
    }

    private fun carregarProdutos(dto: TransacaoRquest): Map<Long, ProdutoEntity> {
        val ids = dto.items
            .map { it.id }
            .distinct()

        require(ids.isNotEmpty()) {
            "Nenhum ID de produto foi informado."
        }

        val produtos = produtoRepository.findAllById(ids).toList()
        val produtosMap = produtos.associateBy { it.id }

        val missingIds = ids.filterNot(produtosMap::containsKey)
        require(missingIds.isEmpty()) {
            "Produtos não encontrados: ${missingIds.joinToString(", ")}"
        }

        return produtosMap
    }


    private fun montarItens(
        dto: TransacaoRquest,
        produtos: Map<Long, ProdutoEntity>
    ): List<TransacaoItemEntity> = dto.items.map { itemDto ->
        val idLong = itemDto.id
        val produto = produtos[idLong]
            ?: throw IllegalArgumentException("Produto não encontrado para ID: ${itemDto.id}")

        val quantidade = (itemDto.quantity ?: 0).takeIf { it > 0 }
            ?: throw IllegalArgumentException("Quantidade inválida para ${itemDto.id}")

        require(produto.ativo) { "Produto '${produto.nome}' está inativo." }
        require(produto.quantidadeEstoque >= quantidade) { "Estoque insuficiente para '${produto.nome}'." }

        produto.quantidadeEstoque -= quantidade

        val valorUnit = produto.preco
        val valorTot = valorUnit.multiply(BigDecimal.valueOf(quantidade.toLong()))

        TransacaoItemEntity(
            quantidade = quantidade,
            valorUnitario = valorUnit,
            valorTotal = valorTot,
            produto = produto,
            transacao = null
        )
    }

    private fun calcularValorTotal(itens: List<TransacaoItemEntity>): BigDecimal =
        itens.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.valorTotal) }

    private fun persistirEstoque(produtos: List<ProdutoEntity>) {
        if (produtos.isNotEmpty()) produtoRepository.saveAll(produtos)
    }
}
