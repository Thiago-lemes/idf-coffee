package org.br.idf.coffee.auth.service

interface PasswordHashService {
    fun hash(raw: String): String
    fun matches(raw: String, hashed: String): Boolean
}
