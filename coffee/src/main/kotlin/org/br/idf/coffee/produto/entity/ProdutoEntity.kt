package org.br.idf.coffee.produto.entity

import jakarta.persistence.*
import org.br.idf.coffee.categoria.entity.CategoriaEntity
import java.math.BigDecimal

@Entity
@Table(name = "produto")
data class ProdutoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "nome", nullable = false)
    val nome: String,

    @Column(name = "descricao")
    val descricao: String? = null,

    @Column(name = "preco", nullable = false)
    val preco: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    val categoria: CategoriaEntity,

    @Column(name = "quantidade_estoque")
    var quantidadeEstoque: Int = 0,

    @Column(name = "ativo")
    val ativo: Boolean = true,
)
