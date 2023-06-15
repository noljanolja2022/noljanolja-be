package com.noljanolja.server.consumer.rest

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
            (accept(MediaType.MULTIPART_FORM_DATA)).nest {
                POST("", conversationHandler::createConversation)
            }
            GET("/react-icons", conversationHandler::getAllReactionIcons)

            "/{conversationId}".nest {
                GET("", conversationHandler::getConversationDetails)
                (accept(MediaType.MULTIPART_FORM_DATA)).nest {
                    PUT("", conversationHandler::updateConversation)
                }

                "/messages".nest {
                    GET("", conversationHandler::getConversationMessages)
                    (accept(MediaType.MULTIPART_FORM_DATA)).nest {
                        POST("", conversationHandler::sendConversationMessages)
                    }
                    "/{messageId}".nest {
                        POST("seen", conversationHandler::seenMessage)
                        PUT("/reactions/{reactionId}", conversationHandler::reactMessage)
                    }
                }

                "/attachments".nest {
                    GET("{attachmentId}", conversationHandler::downloadConversationAttachment)
                }

                "/participants".nest {
                    PUT("", conversationHandler::addMemberToConversation)
                    DELETE("", conversationHandler::removeMemberFromConversation)
                }

                PUT("/admin", conversationHandler::assignAdminToConversation)
            }
        }
    }
}