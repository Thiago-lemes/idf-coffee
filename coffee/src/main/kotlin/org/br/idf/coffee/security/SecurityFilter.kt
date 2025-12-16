package org.br.idf.coffee.security

import org.br.idf.coffee.usuario.entity.UsuarioDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.br.idf.coffee.usuario.repository.UsuarioRepository

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
        val token = recuperarToken(request)
        token?.let {
            // check blacklist first
            if (tokenBlacklistService.isBlacklisted(it)) {
                filterChain.doFilter(request, response)
                return
            }

            val login = tokenService.validateToken(it)
            if (login.isBlank()) {
                filterChain.doFilter(request, response)
                return
            }
            val usuarioEntity = userRepository.findByEmail(login).orElse(null)
            usuarioEntity?.let {
                val usuarioDetails = UsuarioDetails(it)
                val auth = UsernamePasswordAuthenticationToken(usuarioDetails, null, usuarioDetails.authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun recuperarToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        return authHeader?.replace("Bearer ", "")
    }
}
