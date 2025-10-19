package org.br.idf.coffee.usuario.dto

import org.br.idf.coffee.usuario.entity.UsuarioEntity

data class UsuarioResponseDTO(
    val id: Long,
    val nome: String,
    val email: String,
) {
    companion object {
        fun fromEntity(usuario: UsuarioEntity): UsuarioResponseDTO {
            return UsuarioResponseDTO(
                id = usuario.id,
                nome = usuario.nome,
                email = usuario.email
            )
        }
    }
}

