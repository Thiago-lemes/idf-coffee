package org.br.idf.coffee.usuario.entity

import jakarta.persistence.*
import org.br.idf.coffee.usuario.entity.enums.Roles
import java.time.LocalDateTime

@Entity
@Table(name = "usuario")
class UsuarioEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "nome", nullable = false)
    var nome: String,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "senha", nullable = false)
    var senha: String,

    @Enumerated(EnumType.STRING)
    var role: Roles = Roles.USUARIO,

    @Column(name = "ativo")
    var ativo: Boolean = true,

    @Column(name = "criado_em")
    var criadoEm: LocalDateTime = LocalDateTime.now()
)
