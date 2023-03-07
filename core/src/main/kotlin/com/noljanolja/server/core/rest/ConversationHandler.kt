package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.rest.request.CreateConversationRequest
import com.noljanolja.server.core.rest.request.SaveMessageRequest
import com.noljanolja.server.core.service.ConversationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
) {
    suspend fun getConversations(request: ServerRequest): ServerResponse {
        val userId = request.queryParamOrNull("userId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val messageLimit = request.queryParamOrNull("messageLimit")?.toLongOrNull()?.takeIf { it > 0 } ?: 20
        val senderLimit = request.queryParamOrNull("senderLimit")?.toLongOrNull()?.takeIf { it > 0 } ?: 4
        val conversations = conversationService.getUserConversations(
            userId = userId,
            messageLimit = messageLimit,
            senderLimit = senderLimit,
        )
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
            participantIds.add(creatorId)
            if (type == Conversation.Type.SINGLE && participantIds.size > 2)
                throw Error.InvalidParticipantsSize
            conversationService.createConversation(
                title = title,
                participantIds = participantIds,
                type = type,
                creatorId = creatorId,
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
        val userId = request.queryParamOrNull("userId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val messageLimit = request.queryParamOrNull("messageLimit")?.toLongOrNull()?.takeIf { it > 0 } ?: 20
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val conversation = conversationService.getConversationDetail(
            conversationId = conversationId,
            userId = userId,
            messageLimit = messageLimit,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversation,
                )
            )
    }

    suspend fun getConversationMessages(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val userId = request.queryParamOrNull("userId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val limit = request.queryParamOrNull("limit")?.toLongOrNull()?.takeIf { it > 0 } ?: 20
        val beforeMessageId = request.queryParamOrNull("beforeMessageId")?.toLongOrNull()
        val afterMessageId = request.queryParamOrNull("afterMessageId")?.toLongOrNull()
        val messages = conversationService.getConversationMessages(
            conversationId = conversationId,
            userId = userId,
            limit = limit,
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

    suspend fun saveConversationMessages(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val payload = request.awaitBodyOrNull<SaveMessageRequest>() ?: throw RequestBodyRequired
        val message = with(payload) {
            conversationService.createMessage(
                conversationId = conversationId,
                senderId = senderId,
                type = type,
                message = message,
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