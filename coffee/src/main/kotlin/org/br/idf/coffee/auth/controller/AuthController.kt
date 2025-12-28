package org.br.idf.coffee.auth.controller

import org.br.idf.coffee.auth.dto.LoginRequestDTO
import org.br.idf.coffee.auth.dto.LoginResponseDTO
import org.br.idf.coffee.usuario.entity.UsuarioDetails
import org.br.idf.coffee.usuario.entity.UsuarioEntity
import org.br.idf.coffee.usuario.service.UsuarioAuthenticationService
import org.br.idf.coffee.fluxo_caixa.service.CaixaRegistradoraService
import org.br.idf.coffee.fluxo_caixa.dto.request.FechamentoCaixaRequest
import org.br.idf.coffee.security.TokenService
import org.br.idf.coffee.security.TokenBlacklistService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: UsuarioAuthenticationService,
    private val tokenService: TokenService,
    private val tokenBlacklistService: TokenBlacklistService,
    private val caixaService: CaixaRegistradoraService
) {
    data class MeResponse(val id: Long, val nome: String, val email: String, val role: String)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDTO): ResponseEntity<LoginResponseDTO> {
        val result = authenticationService.authenticate(request.email, request.senha)
        return ResponseEntity.ok(LoginResponseDTO(result.token))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authorization: String?, @RequestParam("caixaId", required = false) caixaId: Long?): ResponseEntity<Any> {
        val token = authorization?.replace("Bearer ", "")
        if (token.isNullOrBlank()) {
            return ResponseEntity.badRequest().body("Token ausente")
        }

        val exp = tokenService.getExpirationInstant(token) ?: java.time.Instant.now().plusSeconds(5)
        tokenBlacklistService.blacklist(token, exp)

        caixaId?.let {
            // perform closing; pass a dummy amount as the service recalculates
            try {
                caixaService.fechamentoDeCaixa(it, FechamentoCaixaRequest(BigDecimal.ZERO))
            } catch (ex: Exception) {
                // swallow exceptions related to caixa closing so logout still succeeds
                ex.printStackTrace()
            }
        }

        return ResponseEntity.ok().build()
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
