package org.br.idf.coffee.usuario.service

import org.br.idf.coffee.security.TokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class UsuarioAuthenticationService(
    private val authenticationManager: AuthenticationManager,
    private val usuarioService: UsuarioService,
    private val tokenService: TokenService
) {
    data class AuthResult(val token: String)

    @Throws(AuthenticationException::class)
    fun authenticate(email: String, senha: String): AuthResult {
        val authToken = UsernamePasswordAuthenticationToken(email, senha)
        try {
            authenticationManager.authenticate(authToken)
        } catch (ex: AuthenticationException) {
            // do not leak whether email exists; provide a friendly message
            throw BadCredentialsException("Email ou senha inválidos")
        }

        val usuario = try {
            usuarioService.getByEmail(email)
        } catch (ex: Exception) {
            // if user lookup fails for some reason, respond with generic credentials error
            throw BadCredentialsException("Email ou senha inválidos")
        }

        val token = tokenService.generateToken(usuario)
        return AuthResult(token)
    }
}
