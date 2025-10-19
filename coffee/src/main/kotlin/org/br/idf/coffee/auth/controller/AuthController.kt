package org.br.idf.coffee.auth.controller

import org.br.idf.coffee.auth.dto.LoginRequestDTO
import org.br.idf.coffee.auth.dto.LoginResponseDTO
import org.br.idf.coffee.usuario.entity.UsuarioDetails
import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.usuario.service.UsuarioAuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: UsuarioAuthenticationService
) {
    data class MeResponse(val id: Long, val nome: String, val email: String, val role: String)
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDTO): ResponseEntity<LoginResponseDTO> {
        return try {
            val result = authenticationService.authenticate(request.email, request.senha)
            ResponseEntity.ok(LoginResponseDTO(result.token))
        } catch (ex: Exception) {
            ResponseEntity.status(401).build()
        }
    }

    @GetMapping("/me")
    fun me(): ResponseEntity<MeResponse> {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth.principal
        return if (principal is UsuarioDetails) {
            val user: UsuarioEntity = principal.user
            ResponseEntity.ok(MeResponse(user.id, user.nome, user.email, user.role.name))
        } else {
            ResponseEntity.status(401).build()
        }
    }
}
