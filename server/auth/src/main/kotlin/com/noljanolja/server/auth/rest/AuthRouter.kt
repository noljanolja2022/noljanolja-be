package com.noljanolja.server.auth.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AuthRouter(
    private val authHandler: AuthHandler,
) {
    companion object {
        const val AUTH_ROUTE = "/api/v1/"
    }

    @Bean
    fun routes() = coRouter {
        (AUTH_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("user-info", authHandler::getUserInfo)
        }
    }
}