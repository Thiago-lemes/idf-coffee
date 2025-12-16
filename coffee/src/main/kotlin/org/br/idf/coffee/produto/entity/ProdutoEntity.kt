package org.br.idf.coffee.produto.entity

import jakarta.persistence.*
import org.br.idf.coffee.categoria.entity.CategoriaEntity
import java.math.BigDecimal

@Entity
@Table(name = "produto")
class ProdutoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "nome", nullable = false)
    var nome: String,

    @Column(name = "descricao")
    var descricao: String? = null,

    @Column(name = "preco_venda", nullable = false)
    var precoVenda: BigDecimal,

    @Column(name = "preco_custo", nullable = false)
    var precoCusto: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    var categoria: CategoriaEntity,

    @Column(name = "quantidade_estoque")
    var quantidadeEstoque: Int = 0,

    @Column(name = "ativo")
    var ativo: Boolean = true,

    @Column(name = "icone")
    var icone: String? = null,
)
