package org.br.idf.coffee.categoria.controller

import org.br.idf.coffee.categoria.dto.CategoriaDTO
import org.br.idf.coffee.categoria.service.CategoriaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categoria")
class CategoriaController(
    private val service: CategoriaService
) {
    @PostMapping
    fun create(@RequestBody dto: CategoriaDTO): ResponseEntity<Any> {
        return try {
            val created = service.register(dto)
            ResponseEntity.status(201).body(created)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<CategoriaDTO>> =
        ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<CategoriaDTO> {
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
    fun update(@PathVariable id: Long, @RequestBody dto: CategoriaDTO): ResponseEntity<Any> {
        return try {
            val updated = service.update(dto, id)
            ResponseEntity.status(201).body(updated)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("error" to ex.message))
        }
    }
}