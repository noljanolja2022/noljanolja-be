package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.rest.request.CreateConversationRequest
import com.noljanolja.server.consumer.rest.request.SaveMessageRequest
import com.noljanolja.server.consumer.service.ConversationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
) {
    suspend fun getConversations(request: ServerRequest): ServerResponse {
        val conversations = conversationService.getUserConversations()
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversations,
                )
            )
    }

    suspend fun createConversation(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateConversationRequest>() ?: throw RequestBodyRequired
        val conversation = with(payload) {
            conversationService.createConversation(
                title = title,
                participantIds = participantIds,
                type = type,
            )
        }
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversation,
                )
            )
    }

    suspend fun getConversationDetails(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val conversation = conversationService.getConversationDetail(
            conversationId = conversationId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversation
                )
            )
    }

    suspend fun getConversationMessages(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val beforeMessageId = request.queryParamOrNull("beforeMessageId")?.toLongOrNull()
        val afterMessageId = request.queryParamOrNull("afterMessageId")?.toLongOrNull()
        val messages = conversationService.getConversationMessages(
            conversationId = conversationId,
            beforeMessageId = beforeMessageId,
            afterMessageId = afterMessageId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = messages,
                )
            )
    }

    suspend fun sendConversationMessages(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<SaveMessageRequest>() ?: throw RequestBodyRequired
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val message = with(payload) {
            conversationService.createMessage(
                message = message,
                type = type,
                conversationId = conversationId,
            )
        }
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = message,
                )
            )
    }
}