package org.br.idf.coffee.usuario.controller

import org.br.idf.coffee.usuario.dto.UsuarioRequestDTO
import org.br.idf.coffee.usuario.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/usuario")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @PostMapping("/register")
    fun register( @RequestBody request: UsuarioRequestDTO): ResponseEntity<Any> {
        val created = usuarioService.register(request)
        val location = URI.create("/usuario/${created.id}")
        return ResponseEntity.created(location).body(created)
    }
}
