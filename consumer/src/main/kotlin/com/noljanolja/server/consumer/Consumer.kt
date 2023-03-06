package com.noljanolja.server.consumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Consumer

fun main(args: Array<String>) {
    runApplication<Consumer>(*args)
}
