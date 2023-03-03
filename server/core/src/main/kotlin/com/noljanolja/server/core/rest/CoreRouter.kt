package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CoreRouter(
    private val coreHandler: CoreHandler,
) {
    companion object {
        const val CORE_USERS_ROUTE = "/api/v1/users"
        const val CORE_ANNOUNCEMENTS_ROUTE = "/api/v1/announcements"
    }

    @Bean
    fun routes() = coRouter {
        (CORE_USERS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", coreHandler::getUsersInfo)
            GET("/{userId}", coreHandler::getUserInfo)
            POST("", coreHandler::upsertUser)
        }
        (CORE_ANNOUNCEMENTS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", coreHandler::getAnnouncements)
            DELETE("{announcementId}", coreHandler::deleteAnnouncement)
            POST("", coreHandler::createAnnouncement)
        }
    }
}