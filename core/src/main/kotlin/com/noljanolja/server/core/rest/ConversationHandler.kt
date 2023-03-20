package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.rest.request.CreateConversationRequest
import com.noljanolja.server.core.rest.request.SaveAttachmentsRequest
import com.noljanolja.server.core.rest.request.SaveMessageRequest
import com.noljanolja.server.core.rest.request.UpdateMessageStatusRequest
import com.noljanolja.server.core.service.ConversationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
) {
    companion object {
        const val QUERY_PARAM_CONVERSATION_ID = "conversationId"
        const val QUERY_PARAM_MESSAGE_ID = "messageId"
        const val QUERY_PARAM_USER_ID = "userId"
    }

    suspend fun getConversations(request: ServerRequest): ServerResponse {
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID)?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
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
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID)?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
        val messageLimit = request.queryParamOrNull("messageLimit")?.toLongOrNull()?.takeIf { it > 0 } ?: 20
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
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
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID)?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
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
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
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

    suspend fun updateMessageStatus(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val messageId = request.pathVariable(QUERY_PARAM_MESSAGE_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_MESSAGE_ID)
        val payload = request.awaitBodyOrNull<UpdateMessageStatusRequest>() ?: throw RequestBodyRequired
        conversationService.updateMessageStatus(
            messageId = messageId,
            conversationId = conversationId,
            seenBy = payload.seenBy,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun saveAttachments(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<SaveAttachmentsRequest>() ?: throw RequestBodyRequired
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val messageId = request.pathVariable(QUERY_PARAM_MESSAGE_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_MESSAGE_ID)
        val message = conversationService.saveAttachments(
            attachments = payload.attachments,
            conversationId = conversationId,
            messageId = messageId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = message,
                )
            )
    }

    suspend fun getAttachmentById(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val attachmentId = request.pathVariable("attachmentId").toLongOrNull()
            ?: throw InvalidParamsException("attachmentId")
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID)?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
        val attachment = conversationService.getAttachmentById(
            userId = userId,
            conversationId = conversationId,
            attachmentId = attachmentId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = attachment,
                )
            )
    }
}