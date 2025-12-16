package org.br.idf.coffee.security

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenBlacklistService {

    private val blacklist = ConcurrentHashMap<String, Instant>()

    fun blacklist(token: String, expiresAt: Instant) {
        blacklist[token] = expiresAt
    }

    fun isBlacklisted(token: String): Boolean {
        val exp = blacklist[token] ?: return false
        if (Instant.now().isAfter(exp)) {
            blacklist.remove(token)
            return false
        }
        return true
    }
}

