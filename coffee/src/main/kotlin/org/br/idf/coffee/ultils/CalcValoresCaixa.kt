package org.br.idf.coffee.ultils

import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CalcValoresCaixa {
    fun calculaFechamentoDeCaixa(
        valorDinheiro: BigDecimal,
        valorInicial: BigDecimal,
        sangria: BigDecimal
    ): BigDecimal {
        if (valorDinheiro > 0.0.toBigDecimal()) {
            return valorDinheiro
                .subtract(sangria)
                .subtract(valorInicial)
        }
        return valorInicial
    }

    fun calculaTotalDeVendas(
        dinheiro: BigDecimal,
        credito: BigDecimal,
        pix: BigDecimal,
        debito: BigDecimal
    ): BigDecimal {
        val total = dinheiro
            .add(debito)
            .add(pix)
            .add(credito)
        return total
    }

    fun calculaSaldoDinheiroCaixa(
        valorInicial: BigDecimal,
        totalVendasDinheiro: BigDecimal,
        totalSangrias: BigDecimal
    ): BigDecimal {
        return valorInicial
            .add(totalVendasDinheiro)
            .subtract(totalSangrias)
    }
}