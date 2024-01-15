package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class NotificationRouter (
    private val notificationHandler: NotificationHandler,
) {
    companion object {
        const val NOTIFICATION_ROUTES = "/api/v1/notification"
    }

    @Bean
    fun notificationRoutes() = coRouter {
        (NOTIFICATION_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", notificationHandler::getNotifications)
            POST("/{notification-id}/read", notificationHandler::readNotification)
            POST("/readAll", notificationHandler::readAllNotifications)
        }
    }
}