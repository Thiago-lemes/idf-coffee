package org.br.idf.coffee.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.br.idf.coffee.usuario.entity.UsuarioDetails
import org.br.idf.coffee.usuario.repository.UsuarioRepository
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityFilter(
    private val tokenService: TokenService,
    private val userRepository: UsuarioRepository,
    private val tokenBlacklistService: TokenBlacklistService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1️⃣ Ignore completamente preflight
        if (request.method == HttpMethod.OPTIONS.name()) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")

        // 2️⃣ Sem token? segue fluxo normal
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.removePrefix("Bearer ").trim()

        // 3️⃣ Token inválido? não autentica, mas não bloqueia aqui
        if (tokenBlacklistService.isBlacklisted(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val login = tokenService.validateToken(token)
        if (login.isBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        val usuario = userRepository.findByEmail(login).orElse(null)
        if (usuario != null) {
            val userDetails = UsuarioDetails(usuario)
            val auth = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )
            SecurityContextHolder.getContext().authentication = auth
        }

        filterChain.doFilter(request, response)
    }
}

