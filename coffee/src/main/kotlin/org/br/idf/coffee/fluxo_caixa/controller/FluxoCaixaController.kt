package org.br.idf.coffee.fluxo_caixa.controller;

import org.br.idf.coffee.fluxo_caixa.dto.request.OpenCaixaRequest
import org.br.idf.coffee.fluxo_caixa.service.FluxoCaixaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/caixa")

public class FluxoCaixaController(
    private val service: FluxoCaixaService
) {
    @GetMapping("/{id}")
    fun getStatusCaixa(@PathVariable id: Long):  ResponseEntity<Any> {
        return try{
          val status = service.getStatusCaixa(id)
            ResponseEntity.status(201).body(status)
        }catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @PostMapping("/abertura")
    fun openFluxoCaixa(@RequestBody dto: OpenCaixaRequest): ResponseEntity<Any> {
        return try {
            val aberturaCaixa = service.aberturaDeCaixa(dto)
            ResponseEntity.status(201).body(aberturaCaixa)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }
}


