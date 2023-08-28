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
            GET("/react-icons", conversationHandler::getAllReactionIcons)
            GET("/details", conversationHandler::getConversationDetails)
            POST("/messages", conversationHandler::createMessageInMultipleConversations)

            "/attachments".nest {
                GET("{attachmentId}", conversationHandler::getAttachmentById)
            }

            "/{conversationId}".nest {
                GET("", conversationHandler::getConversationDetail)
                PUT("", conversationHandler::updateConversation)
                GET("/attachments", conversationHandler::getConversationAttachments)

                "/messages".nest {
                    GET("", conversationHandler::getConversationMessages)

                    "/{messageId}".nest {
                        PUT("", conversationHandler::updateMessageStatus)
                        POST("/attachments", conversationHandler::saveAttachments)
                        PUT("/reactions/{reactionId}", conversationHandler::reactMessage)
                        DELETE("/reactions", conversationHandler::clearAllReactions)
                        DELETE("", conversationHandler::removeMessage)
                    }
                }

                "/participants".nest {
                    PUT("", conversationHandler::addMemberToConversation)
                    DELETE("", conversationHandler::removeMemberFromConversation)
                }

                "/admin".nest {
                    PUT("", conversationHandler::assignConversationAdmin)
                }
            }
        }
    }
}
