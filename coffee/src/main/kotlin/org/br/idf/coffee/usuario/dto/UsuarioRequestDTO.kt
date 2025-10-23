package org.br.idf.coffee.usuario.dto


import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.usuario.entity.enums.Roles
import java.time.LocalDateTime
import java.util.*

data class UsuarioRequestDTO(
    val nome: String,
    val email: String,
    val senha: String,
) {
    fun toEntity() = UsuarioEntity(
        nome = nome.uppercase(Locale.getDefault()),
        email = email,
        senha = senha,
        role = Roles.USUARIO,
        ativo = true,
        criadoEm = LocalDateTime.now()
    )
}
