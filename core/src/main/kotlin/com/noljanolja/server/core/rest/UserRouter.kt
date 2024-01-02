package com.noljanolja.server.core.rest

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
            GET("", userHandler::getUsers)
            POST("", userHandler::upsertUser)

            "/{userId}".nest {
                GET("", userHandler::getUserDetails)
                GET("/contact-detail", userHandler::getUserContactDetail)
                PATCH("", userHandler::updateUser)
                DELETE("", userHandler::deleteUser)
                GET("/black-list", userHandler::getBlackListOfUser)
                PUT("/block", userHandler::userBlockUser)
                PUT("/referral", userHandler::assignReferral)

                "/contacts".nest {
                    POST("", userHandler::upsertUserContacts)
                    POST("/invite", userHandler::sendFriendRequest)
                }
            }
        }
    }
}