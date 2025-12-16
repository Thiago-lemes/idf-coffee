package org.br.idf.coffee.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfigurations(
    private val securityFilter: SecurityFilter,
    private val environment: Environment
) {

    private fun isDevOrTestProfile(): Boolean {
        val active = environment.activeProfiles
        return active.any { it.equals("dev", ignoreCase = true) || it.equals("test", ignoreCase = true) }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            // use patterns / lists to avoid deprecated single-add methods
            allowedOriginPatterns = listOf("http://localhost:3000")
            allowCredentials = true
            // use explicit method names to avoid accessing enum internals
            allowedMethods = listOf("POST", "GET", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*", "Authorization", "Content-Type", "Access-Control-Allow-Origin")
            exposedHeaders = listOf("Access-Control-Allow-Origin")
        }
        source.registerCorsConfiguration("/**", config)

        val isDev = isDevOrTestProfile()

        // configure headers to allow H2 console frames when running in dev/test
        if (isDev) {
            http.headers { headers -> headers.frameOptions { it.disable() } }
        }

        return http
            .cors { it.configurationSource(source) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**").permitAll()
                it.requestMatchers("/usuario/register").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
