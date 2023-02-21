package com.noljanolja.server.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Admin

fun main(args: Array<String>) {
    runApplication<Admin>(*args)
}
