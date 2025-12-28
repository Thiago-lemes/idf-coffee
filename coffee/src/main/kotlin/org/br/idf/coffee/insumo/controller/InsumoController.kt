package org.br.idf.coffee.insumo.controller

import org.br.idf.coffee.insumo.dto.InsumoRequest
import org.br.idf.coffee.insumo.dto.InsumoResponse
import org.br.idf.coffee.insumo.service.InsumoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/insumos")
class InsumoController(
    private val service: InsumoService
) {
    @PostMapping
    fun registraInsumo(@RequestBody request: InsumoRequest): ResponseEntity<InsumoResponse> {
        val insumo = service.registraInsumo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(insumo)
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<InsumoResponse>> =
        ResponseEntity.ok(service.findAll())

    @RestControllerAdvice
    class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException::class)
        fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
            return ResponseEntity
                .badRequest()
                .body(ErrorResponse(ex.message ?: "Erro inválido"))
        }
    }

    data class ErrorResponse(
        val message: String
    )

}