package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.rest.request.*
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
        const val QUERY_PARAM_PARTICIPANT_ID = "participantIds"
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
                imageUrl = imageUrl,
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

    suspend fun updateConversation(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpdateConversationRequest>() ?: throw RequestBodyRequired
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val conversation = with(payload) {
            conversationService.updateConversation(
                id = conversationId,
                updatedTitle = title,
                updatedParticipantIds = participantIds,
                updatedImageUrl = imageUrl,
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

    suspend fun getConversationDetail(request: ServerRequest): ServerResponse {
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID)?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
        val messageLimit = request.queryParamOrNull("messageLimit")?.toLongOrNull()?.takeIf { it >= 0 } ?: 20
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val messageId = request.queryParamOrNull("messageId")?.toLongOrNull()
        val conversation = conversationService.getConversationDetail(
            conversationId = conversationId,
            userId = userId,
            messageLimit = messageLimit,
            messageId = messageId,
        )
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
        val messageLimit = request.queryParamOrNull("messageLimit")?.toLongOrNull()?.takeIf { it >= 0 } ?: 20
        val conversationIds = request.queryParamOrNull("conversationIds").orEmpty().split(",")
            .mapNotNull { it.toLongOrNull() }
            .ifEmpty { throw InvalidParamsException("conversationId") }
        val conversations = conversationService.getConversationsDetails(
            conversationIds = conversationIds,
            userId = userId,
            messageLimit = messageLimit,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversations,
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

    suspend fun createMessageInMultipleConversations(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<SaveMessageRequest>() ?: throw RequestBodyRequired
        val messages = with(payload) {
            conversationService.createMessageInMultipleConversations(
                conversationIds = conversationIds,
                senderId = senderId,
                type = type,
                message = message,
                shareMessageId = shareMessageId,
                replyToMessageId = replyToMessageId,
                shareVideoId = shareVideoId,
            )
        }
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = messages,
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
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val payload = request.awaitBodyOrNull<SaveAttachmentsRequest>() ?: throw RequestBodyRequired
        val message = conversationService.saveAttachments(
            attachments = payload.attachments,
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

    suspend fun addMemberToConversation(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val payload = request.awaitBodyOrNull<UpdateConversationParticipantsRequest>() ?: throw RequestBodyRequired
        val updatedParticipantIds =
            conversationService.addConversationParticipants(conversationId, payload.userId, payload.participantIds)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = updatedParticipantIds
            )
        )
    }

    suspend fun removeMemberFromConversation(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val participantIds = request.queryParamOrNull(QUERY_PARAM_PARTICIPANT_ID) ?: throw InvalidParamsException(
            QUERY_PARAM_PARTICIPANT_ID
        )
        val userId = request.queryParamOrNull(QUERY_PARAM_USER_ID) ?: throw InvalidParamsException(QUERY_PARAM_USER_ID)
        conversationService.removeConversationMember(conversationId, userId, participantIds.split(","))
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = participantIds.split(",")
            )
        )
    }

    suspend fun assignConversationAdmin(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val payload = request.awaitBodyOrNull<UpdateConversationAdminRequest>() ?: throw RequestBodyRequired
        val newAdminId =
            conversationService.assignConversationAdmin(conversationId, payload.adminId, payload.assigneeId)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = newAdminId
            )
        )
    }

    suspend fun reactMessage(request: ServerRequest): ServerResponse {
        val reactionId = request.pathVariable("reactionId").toLongOrNull() ?: throw InvalidParamsException("reactionId")
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val participantId = request.queryParamOrNull("participantId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("participantId")
        val conversationId =
            request.pathVariable("conversationId").toLongOrNull() ?: throw InvalidParamsException("conversationId")
        conversationService.reactMessage(
            participantId = participantId,
            reactionId = reactionId,
            messageId = messageId,
            conversationId = conversationId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun clearAllReactions(request: ServerRequest): ServerResponse {
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val participantId = request.queryParamOrNull("participantId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("participantId")
        val conversationId =
            request.pathVariable("conversationId").toLongOrNull() ?: throw InvalidParamsException("conversationId")
        conversationService.clearAllReactions(
            participantId = participantId,
            messageId = messageId,
            conversationId = conversationId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getAllReactionIcons(request: ServerRequest): ServerResponse {
        val reactionIcons = conversationService.getAllReactions()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = reactionIcons,
                )
            )
    }

    suspend fun removeMessage(request: ServerRequest): ServerResponse {
        val messageId = request.pathVariable("messageId").toLongOrNull()
            ?: throw InvalidParamsException("messageId")
        val userId = request.queryParamOrNull("userId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val conversationId =
            request.pathVariable("conversationId").toLongOrNull() ?: throw InvalidParamsException("conversationId")
        val removeForSelfOnly = request.queryParamOrNull("removeForSelfOnly")?.toBooleanStrictOrNull()
            ?: throw InvalidParamsException("removeForSelfOnly")
        conversationService.removeMessage(
            removeForSelfOnly = removeForSelfOnly,
            userId = userId,
            messageId = messageId,
            conversationId = conversationId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>(),
            )
    }
}