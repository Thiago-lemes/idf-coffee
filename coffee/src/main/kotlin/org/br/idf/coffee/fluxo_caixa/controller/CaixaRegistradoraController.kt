package org.br.idf.coffee.fluxo_caixa.controller;

import org.br.idf.coffee.fluxo_caixa.dto.request.AberturaCaixaRequest
import org.br.idf.coffee.fluxo_caixa.dto.request.FechamentoCaixaRequest
import org.br.idf.coffee.fluxo_caixa.dto.request.SangriaRequest
import org.br.idf.coffee.fluxo_caixa.service.CaixaRegistradoraService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caixa")

class CaixaRegistradoraController(
    private val service: CaixaRegistradoraService
) {
    @GetMapping("/{id}")
    fun getStatusCaixa(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val status = service.getStatusCaixa(id)
            ResponseEntity.status(201).body(status)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @PostMapping("/abertura")
    fun aberturaCaixa(@RequestBody request: AberturaCaixaRequest): ResponseEntity<Any> {
        return try {
            val aberturaCaixa = service.aberturaDeCaixa(request)
            ResponseEntity.status(201).body(aberturaCaixa)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @PostMapping("fechar/{id}")
    fun fechamentoCaixa(@PathVariable id: Long, @RequestBody request: FechamentoCaixaRequest): ResponseEntity<Any> {
        return try {
            val status = service.fechamentoDeCaixa(id, request)
            ResponseEntity.status(201).body(status)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @PostMapping("sangria/{id}")
    fun sangria(@PathVariable id: Long, @RequestBody request: SangriaRequest): ResponseEntity<Any> {
        return try {
            val status = service.sangriaCaixa(id, request)
            ResponseEntity.status(201).body(status)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

}


