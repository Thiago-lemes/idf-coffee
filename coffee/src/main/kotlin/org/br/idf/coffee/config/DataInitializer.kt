package org.br.idf.coffee.config

import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.usuario.entity.enums.Roles
import org.br.idf.coffee.usuario.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DataInitializer(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val email = "admin@local"
        if (usuarioRepository.findByEmail(email).isEmpty) {
            val usuario = UsuarioEntity(
                nome = "Admin",
                email = email,
                senha = passwordEncoder.encode("admin"),
                role = Roles.ADMIN,
                ativo = true
            )
            usuarioRepository.save(usuario)
            println("[DataInitializer] Created dev user: $email / admin")
        } else {
            println("[DataInitializer] Dev user already exists: $email")
        }
    }
}

