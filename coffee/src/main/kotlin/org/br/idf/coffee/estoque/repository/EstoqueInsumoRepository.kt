package org.br.idf.coffee.estoque.repository

import org.br.idf.coffee.estoque.entity.EstoqueInsumoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EstoqueInsumoRepository : JpaRepository<EstoqueInsumoEntity, Long> {}

