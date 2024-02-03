package com.noljanolja.server.admin.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ConversationRouter(
    private val conversationHandler: ConversationHandler
) {
    companion object {
        const val CONVERSATIONS_ROUTE = "/api/v1/conversations"
    }

    @Bean
    fun conversationRoutes() = coRouter {
        (CONVERSATIONS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/analytics", conversationHandler::getConversationAnalytics)
        }
    }
}