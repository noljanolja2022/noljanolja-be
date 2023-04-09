package com.noljanolja.server.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication(scanBasePackages = ["com.noljanolja.server"])
@EntityScan(basePackages = ["com.noljanolja.server"])
@EnableR2dbcRepositories(basePackages = ["com.noljanolja.server"])
@EnableR2dbcAuditing
class Core

fun main(args: Array<String>) {
    runApplication<Core>(*args)
}