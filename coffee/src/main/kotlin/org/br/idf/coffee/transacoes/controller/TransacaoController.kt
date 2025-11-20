package org.br.idf.coffee.transacoes.controller

import org.br.idf.coffee.transacoes.dto.TransacaoRquest
import org.br.idf.coffee.transacoes.service.TransacaoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transacao")
class TransacaoController (private val service: TransacaoService) {
    @PostMapping("/{id}")
    fun transacaoVenda(@PathVariable id: Long, @RequestBody request: TransacaoRquest): ResponseEntity<Any> {
        return try {
            service.criarTransacao(request, id)
            ResponseEntity.status(201).body(mapOf("message" to "Transação realizada com sucesso"))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }
}