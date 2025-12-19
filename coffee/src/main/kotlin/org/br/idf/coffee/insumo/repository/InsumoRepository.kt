package org.br.idf.coffee.insumo.repository

import org.br.idf.coffee.insumo.entity.InsumoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InsumoRepository : JpaRepository<InsumoEntity, Long>

