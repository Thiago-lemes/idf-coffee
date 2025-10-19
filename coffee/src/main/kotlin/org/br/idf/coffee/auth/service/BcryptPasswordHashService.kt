package org.br.idf.coffee.security

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class BcryptPasswordHashService(
    private val passwordEncoder: PasswordEncoder
) : PasswordHashService {
    override fun hash(raw: String): String = passwordEncoder.encode(raw)

    override fun matches(raw: String, hashed: String): Boolean = passwordEncoder.matches(raw, hashed)
}
