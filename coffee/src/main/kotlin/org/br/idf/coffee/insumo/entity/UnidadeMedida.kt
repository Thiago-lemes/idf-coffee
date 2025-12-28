package org.br.idf.coffee.insumo.entity

enum class UnidadeMedida {
    UN, g, KG, mL, L;

    fun unidadeBase(): UnidadeMedida = when {
        isPeso() -> g
        isVolume() -> mL
        else -> UN
    }

    fun isPeso() = this == g || this == KG
    fun isVolume() = this == mL || this == L
}
