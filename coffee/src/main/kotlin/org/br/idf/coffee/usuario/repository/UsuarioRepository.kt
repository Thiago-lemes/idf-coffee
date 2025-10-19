package org.br.idf.coffee.usuario.repository

import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UsuarioRepository : JpaRepository<UsuarioEntity, Long> {
    fun findByEmail(email: String): Optional<UsuarioEntity>
}

