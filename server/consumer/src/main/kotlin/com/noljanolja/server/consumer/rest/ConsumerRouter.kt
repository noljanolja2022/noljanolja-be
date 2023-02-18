package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ConsumerRouter(
    private val consumerHandler: ConsumerHandler,
) {
    companion object {
        const val CONSUMER_ROUTER = "/api/v1"
    }

    @Bean
    fun routes() = coRouter {
        (CONSUMER_ROUTER and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/users/me", consumerHandler::getMyInfo)
        }
    }
}