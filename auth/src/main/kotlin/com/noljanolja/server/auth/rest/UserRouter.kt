package com.noljanolja.server.auth.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
) {
    companion object {
        const val USER_ROUTE = "/api/v1/users"
    }

    @Bean
    fun routes() = coRouter {
        (USER_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", userHandler::getUser)
            DELETE("", userHandler::deleteUser)
        }
    }
}