package com.noljanolja.server.consumer.rest

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
            GET("", userHandler::findUserByPhone)
            "/me".nest {
                GET("", userHandler::getCurrentUser)
                PUT("", userHandler::updateCurrentUser)
                DELETE("", userHandler::deleteCurrentUser)
                POST("/contacts", userHandler::syncCurrentUserContact)
                GET("/contacts", userHandler::getCurrentUserContacts)
            }
        }
        (USERS_ROUTE and accept(MediaType.MULTIPART_FORM_DATA)).nest {
            "/me".nest {
                POST("", userHandler::uploadCurrentUserData)
            }
        }
    }
}