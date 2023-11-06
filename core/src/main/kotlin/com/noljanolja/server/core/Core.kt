package com.noljanolja.server.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication(scanBasePackages = ["com.noljanolja.server", "com.nolgobuljia.server"])
@EntityScan(basePackages = ["com.noljanolja.server"])
@EnableR2dbcRepositories(basePackages = ["com.noljanolja.server"])
@EnableR2dbcAuditing
class Core

fun main(args: Array<String>) {
    runApplication<Core>(*args)
}