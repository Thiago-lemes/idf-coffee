package org.br.idf.coffee.usuario.entity

import jakarta.persistence.*
import org.br.idf.coffee.usuario.entity.enums.Roles
import java.time.LocalDateTime

@Entity
@Table(name = "usuario")
data class UsuarioEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "nome", nullable = false)
    val nome: String,

    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @Column(name = "senha", nullable = false)
    val senha: String,

    @Enumerated(EnumType.STRING)
    val role: Roles = Roles.USUARIO,

    @Column(name = "ativo")
    val ativo: Boolean = true,

    @Column(name = "criado_em")
    val criadoEm: LocalDateTime = LocalDateTime.now()
)
