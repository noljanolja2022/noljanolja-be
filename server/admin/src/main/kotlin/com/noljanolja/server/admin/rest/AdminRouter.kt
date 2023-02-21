package com.noljanolja.server.admin.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AdminRouter(
    private val adminHandler: AdminHandler,
) {
    companion object {
        const val ADMIN_ROUTER = "/api/v1"
    }

    @Bean
    fun routes() = coRouter {
        (ADMIN_ROUTER and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/users/me", adminHandler::getMyInfo)
        }
    }
}