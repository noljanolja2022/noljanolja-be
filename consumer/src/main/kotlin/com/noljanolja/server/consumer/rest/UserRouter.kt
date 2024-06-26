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
                PUT("/referral", userHandler::assignReferral)
                POST("/contacts", userHandler::syncCurrentUserContact)
                GET("/contacts", userHandler::getCurrentUserContacts)
                GET("/contacts/{userId}", userHandler::getCurrentUserContactDetail)
                POST("/contacts/invite", userHandler::sendFriendRequest)
                GET("/black-list", userHandler::getBlackList)
                PUT("/block", userHandler::blockUser)
                POST("/checkin", userHandler::checkin)
                GET("/checkin-progresses", userHandler::getMyCheckinProgresses)
            }
        }
        (USERS_ROUTE and accept(MediaType.MULTIPART_FORM_DATA)).nest {
            "/me".nest {
                POST("", userHandler::uploadCurrentUserData)
            }
        }
    }
}