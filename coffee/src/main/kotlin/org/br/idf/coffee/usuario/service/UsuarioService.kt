package org.br.idf.coffee.usuario.service

import org.br.idf.coffee.usuario.dto.UsuarioRequestDTO
import org.br.idf.coffee.usuario.dto.UsuarioResponseDTO
import org.br.idf.coffee.usuario.repository.UsuarioRepository
import org.br.idf.coffee.usuario.entity.UsuarioDetails
import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.security.PasswordHashService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordHashService: PasswordHashService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val usuario =
            usuarioRepository.findByEmail(username).orElseThrow {
                UsernameNotFoundException("User not found")
            }
        return UsuarioDetails(usuario)
    }

    fun register(dto: UsuarioRequestDTO): UsuarioResponseDTO {
        check(!usuarioRepository.findByEmail(dto.email).isPresent) { "Email já cadastrado" }

        val usuarioAtualizado: UsuarioEntity = dto.toEntity().copy(
            senha = passwordHashService.hash(dto.senha)
        )
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuarioAtualizado))
    }

    fun getByEmail(email: String): UsuarioEntity =
        usuarioRepository.findByEmail(email).orElseThrow { UsernameNotFoundException("User not found") }

    fun deleteById(id: Long) {
        usuarioRepository.deleteById(id)
    }

    fun update(usuario: UsuarioEntity): UsuarioEntity {
        // basic update: expects id present; in real app validate fields
        return usuarioRepository.save(usuario)
    }
}
