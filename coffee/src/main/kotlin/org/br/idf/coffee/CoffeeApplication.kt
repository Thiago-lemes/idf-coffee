package org.br.idf.coffee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoffeeApplication

fun main(args: Array<String>) {
	runApplication<CoffeeApplication>(*args)
}
