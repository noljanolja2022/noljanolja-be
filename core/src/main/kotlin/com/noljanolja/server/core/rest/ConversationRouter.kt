package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ConversationRouter(
    private val conversationHandler: ConversationHandler,
) {
    companion object {
        const val CONVERSATIONS_ROUTE = "/api/v1/conversations"
    }

    @Bean
    fun conversationRoutes() = coRouter {
        (CONVERSATIONS_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", conversationHandler::getConversations)
            POST("", conversationHandler::createConversation)

            "/{conversationId}".nest {
                GET("", conversationHandler::getConversationDetails)

                "/messages".nest {
                    GET("", conversationHandler::getConversationMessages)
                    POST("", conversationHandler::saveConversationMessages)
                    PUT("{messageId}", conversationHandler::updateMessageStatus)
                }
            }
        }
    }
}
