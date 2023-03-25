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
                PUT("", conversationHandler::updateConversation)

                "/messages".nest {
                    GET("", conversationHandler::getConversationMessages)
                    POST("", conversationHandler::saveConversationMessages)
                    "/{messageId}".nest {
                        PUT("", conversationHandler::updateMessageStatus)
                        POST("/attachments", conversationHandler::saveAttachments)
                    }
                }

                "/attachments".nest {
                    GET("{attachmentId}", conversationHandler::getAttachmentById)
                }
            }
        }
    }
}
