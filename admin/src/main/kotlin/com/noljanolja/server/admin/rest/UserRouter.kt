package com.noljanolja.server.admin.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
) {
    companion object {
        const val USERS_ROUTE = "/api/v1/users"
    }

    @Bean
    fun userRoutes() = coRouter {
        (USERS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/me", userHandler::getCurrentUser)
            GET("", userHandler::getUsers)
            PUT("", userHandler::createAdminUser)
            PATCH("/{id}", userHandler::updateUser)
            DELETE("/{id}", userHandler::deleteUser)
        }
    }
}