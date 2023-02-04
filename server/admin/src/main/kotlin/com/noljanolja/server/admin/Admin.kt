package com.noljanolja.server.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Admin

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Admin>(*args)
}
