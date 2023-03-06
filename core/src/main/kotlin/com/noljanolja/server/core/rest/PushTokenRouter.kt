package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PushTokenRouter(
    private val pushTokenHandler: PushTokenHandler,
) {
    companion object {
        const val PUSH_TOKENS_ROUTE = "/api/v1/push-tokens"
    }

    @Bean
    fun routes() = coRouter {
        (PUSH_TOKENS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", pushTokenHandler::updatePushToken)
        }
    }
}