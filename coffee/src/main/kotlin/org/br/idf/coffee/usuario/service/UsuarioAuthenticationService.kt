package org.br.idf.coffee.usuario.service

import org.br.idf.coffee.security.TokenService
import org.springframework.security.authentication.AuthenticationManager
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
        authenticationManager.authenticate(authToken)

        val usuario = usuarioService.getByEmail(email)
        val token = tokenService.generateToken(usuario)
        return AuthResult(token)
    }
}
