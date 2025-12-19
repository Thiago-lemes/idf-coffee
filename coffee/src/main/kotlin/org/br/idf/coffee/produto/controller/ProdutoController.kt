package org.br.idf.coffee.produto.controller

import org.br.idf.coffee.produto.dto.ProdutoRequest
import org.br.idf.coffee.produto.dto.ProdutoResponse
import org.br.idf.coffee.produto.service.ProdutoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/produto")
class ProdutoController(
    private val service: ProdutoService
) {
    @PostMapping()
    fun create(@RequestBody dto: ProdutoRequest): ResponseEntity<Any> {
        return try {
            val created = service.registrarProduto(dto)
            ResponseEntity.status(201).body(created)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<ProdutoResponse>> =
        ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ProdutoResponse> {
        val categoria = service.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(categoria)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        service.delete(id)
        return ResponseEntity.ok(mapOf("message" to "Categoria removida com sucesso"))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ProdutoRequest): ResponseEntity<Any> {
        return try {
            val updated = service.update(id, dto)
            ResponseEntity.status(201).body(updated)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }
}