package com.noljanolja.server.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Core

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Core>(*args)
}
