package org.br.idf.coffee.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
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
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return http
            .cors { it.configurationSource(source) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
}
