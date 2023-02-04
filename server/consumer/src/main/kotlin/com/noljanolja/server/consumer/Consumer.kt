package com.noljanolja.server.consumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Consumer

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Consumer>(*args)
}
