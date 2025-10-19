package org.br.idf.coffee.security

interface PasswordHashService {
    fun hash(raw: String): String
    fun matches(raw: String, hashed: String): Boolean
}
