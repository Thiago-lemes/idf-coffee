package org.br.idf.coffee.insumo.entity

enum class UnidadeMedida {
    UN, G, KG, ML, L;

    fun unidadeBase(): UnidadeMedida = when {
        isPeso() -> G
        isVolume() -> ML
        else -> UN
    }

    fun isPeso() = this == G || this == KG
    fun isVolume() = this == ML || this == L
}
