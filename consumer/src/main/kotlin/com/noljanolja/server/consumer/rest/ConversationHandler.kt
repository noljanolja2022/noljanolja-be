package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.exception.Error
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.rest.request.UpdateAdminOfConversationReq
import com.noljanolja.server.consumer.rest.request.UpdateMemberOfConversationRequest
import com.noljanolja.server.consumer.service.ConversationService
import com.noljanolja.server.consumer.service.GoogleStorageService
import com.noljanolja.server.consumer.utils.toFileAttachment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
    private val googleStorageService: GoogleStorageService,
) {
    companion object {
        const val QUERY_PARAM_CONVERSATION_ID = "conversationId"
        const val QUERY_PARAM_MESSAGE_ID = "messageId"
        const val QUERY_PARAM_PARTICIPANT_ID = "participantIds"
        const val DOWNLOAD_FILE_HEADER = "Noljanolja-File-Download"
        const val MAX_ATTACHMENTS_SIZE = 10
    }

    suspend fun getConversations(request: ServerRequest): ServerResponse {
        val conversations = conversationService.getUserConversations(AuthUserHolder.awaitUser().id)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversations,
                )
            )
    }

    suspend fun createConversation(request: ServerRequest): ServerResponse {
        val payload = request.awaitMultipartData()
        val type = payload.getFirst("type")?.content()?.awaitSingle()?.let {
            Conversation.Type.valueOf(String(it.asInputStream().readAllBytes()))
        } ?: Conversation.Type.SINGLE
        val title = payload.getFirst("title")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val participantIds = payload["participantIds"]?.mapTo(mutableSetOf()) {
            String(it.content().awaitSingle().asInputStream().readAllBytes())
        }.orEmpty()
        val image = (payload["image"] as? List<FilePart>)?.firstOrNull()
        val conversation = conversationService.createConversation(
            userId = AuthUserHolder.awaitUser().id,
            title = title,
            participantIds = participantIds,
            type = type,
            image = image?.toFileAttachment(),
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = conversation,
                )
            )
    }

    suspend fun updateConversation(request: ServerRequest): ServerResponse {
        val payload = request.awaitMultipartData()
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val title = payload.getFirst("title")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }
        val participantIds = payload["participantIds"]?.mapTo(mutableSetOf()) {
            String(it.content().awaitSingle().asInputStream().readAllBytes())
        }
        val image = (payload["image"] as? List<FilePart>)?.firstOrNull()
        val conversation = conversationService.updateConversation(
            userId = AuthUserHolder.awaitUser().id,
            title = title,
            participantIds = participantIds,
            image = image?.toFileAttachment(),
            conversationId = conversationId,
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
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val conversation = conversationService.getConversationDetail(
            userId = AuthUserHolder.awaitUser().id,
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
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val beforeMessageId = request.queryParamOrNull("beforeMessageId")?.toLongOrNull()
        val afterMessageId = request.queryParamOrNull("afterMessageId")?.toLongOrNull()
        val messages = conversationService.getConversationMessages(
            userId = AuthUserHolder.awaitUser().id,
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

    suspend fun sendMessage(request: ServerRequest): ServerResponse {
        val payload = request.awaitMultipartData()
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val message = payload.getFirst("message")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val type = payload.getFirst("type")?.content()?.awaitSingle()?.let {
            Message.Type.valueOf(String(it.asInputStream().readAllBytes()))
        } ?: Message.Type.PLAINTEXT
        val attachments = (payload["attachments"] as? List<FilePart>)?.also {
            if (it.size > MAX_ATTACHMENTS_SIZE) throw Error.ExceedMaxAttachmentsSize
        }.orEmpty()
        val localId = payload.getFirst("localId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val shareMessageId = payload.getFirst("shareMessageId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes()).toLongOrNull()
        }
        val replyToMessageId = payload.getFirst("replyToMessageId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes()).toLongOrNull()
        }
        val data = conversationService.createMessage(
            userId = AuthUserHolder.awaitUser().id,
            message = message,
            type = type,
            conversationId = conversationId,
            localId = localId,
            attachments = attachments.map { it.toFileAttachment() },
            shareMessageId = shareMessageId,
            replyToMessageId = replyToMessageId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = data,
                )
            )
    }

    suspend fun shareMessage(request: ServerRequest): ServerResponse {
        val payload = request.awaitMultipartData()
        val message = payload.getFirst("message")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val type = payload.getFirst("type")?.content()?.awaitSingle()?.let {
            Message.Type.valueOf(String(it.asInputStream().readAllBytes()))
        } ?: Message.Type.PLAINTEXT
        val attachments = (payload["attachments"] as? List<FilePart>)?.also {
            if (it.size > MAX_ATTACHMENTS_SIZE) throw Error.ExceedMaxAttachmentsSize
        }.orEmpty()
        val localId = payload.getFirst("localId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val shareMessageId = payload.getFirst("shareMessageId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes()).toLongOrNull()
        }
        val conversationIds = payload["conversationIds"]?.mapNotNullTo(mutableSetOf()) {
            String(it.content().awaitSingle().asInputStream().readAllBytes()).toLongOrNull()
        }.orEmpty().ifEmpty { throw InvalidParamsException("conversationIds") }
        val shareVideoId = payload.getFirst("shareVideoId")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }
        val messages = conversationService.shareMessage(
            userId = AuthUserHolder.awaitUser().id,
            message = message,
            type = type,
            conversationIds = conversationIds.toList(),
            localId = localId,
            attachments = attachments.map { it.toFileAttachment() },
            shareMessageId = shareMessageId,
            shareVideoId = shareVideoId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = messages,
                )
            )
    }

    suspend fun seenMessage(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val messageId = request.pathVariable(QUERY_PARAM_MESSAGE_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_MESSAGE_ID)
        val userId = AuthUserHolder.awaitUser().id
        CoroutineScope(Dispatchers.Default).launch {
            conversationService.seenMessage(
                userId = userId,
                messageId = messageId,
                conversationId = conversationId,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun downloadConversationAttachmentByID(request: ServerRequest): ServerResponse {
        val attachmentId = request.pathVariable("attachmentId").toLongOrNull()
            ?: throw InvalidParamsException("attachmentId")
        val attachment = conversationService.getAttachmentById(
            attachmentId = attachmentId
        )
        val resourceInfo = googleStorageService.getResource(
            "conversations/${attachment.name}"
        )
        return ServerResponse.ok()
            .header(HttpHeaders.CONTENT_TYPE, resourceInfo.contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${attachment.name}")
            .header(DOWNLOAD_FILE_HEADER, attachment.name)
            .bodyValueAndAwait(
                InputStreamResource(resourceInfo.data)
            )
    }

    suspend fun downloadConversationAttachment(request: ServerRequest): ServerResponse {
        val attachmentName = request.pathVariable("attachmentName")
            .ifBlank { throw InvalidParamsException("attachmentName") }
        val resourceInfo = googleStorageService.getResource(
            "conversations/${attachmentName}"
        )
        return ServerResponse.ok()
            .header(HttpHeaders.CONTENT_TYPE, resourceInfo.contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${attachmentName}")
            .header(DOWNLOAD_FILE_HEADER, attachmentName)
            .bodyValueAndAwait(
                InputStreamResource(resourceInfo.data)
            )
    }

    suspend fun addMemberToConversation(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val req = request.awaitBodyOrNull<UpdateMemberOfConversationRequest>() ?: throw RequestBodyRequired
        conversationService.addMemberToConversation(
            AuthUserHolder.awaitUser().id,
            conversationId,
            req.participantIds
        )
        return ServerResponse.ok().bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun removeMemberFromConversation(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val participantIds = request.queryParamOrNull(QUERY_PARAM_PARTICIPANT_ID) ?: throw InvalidParamsException(
            QUERY_PARAM_PARTICIPANT_ID
        )
        conversationService.removeMemberFromConversation(
            AuthUserHolder.awaitUser().id,
            conversationId,
            participantIds.split(",")
        )
        return ServerResponse.ok().bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun assignAdminToConversation(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val req = request.awaitBodyOrNull<UpdateAdminOfConversationReq>() ?: throw RequestBodyRequired
        val res =
            conversationService.updateAdminOfConversation(AuthUserHolder.awaitUser().id, conversationId, req.assigneeId)
        return ServerResponse.ok().bodyValueAndAwait(Response(data = res))
    }

    suspend fun reactMessage(request: ServerRequest): ServerResponse {
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val reactionId = request.pathVariable("reactionId").toLongOrNull() ?: throw InvalidParamsException("reactionId")
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        conversationService.reactMessage(
            userId = AuthUserHolder.awaitUser().id,
            messageId = messageId,
            reactionId = reactionId,
            conversationId = conversationId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun clearAllReactions(request: ServerRequest): ServerResponse {
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        conversationService.clearAllReactions(
            userId = AuthUserHolder.awaitUser().id,
            messageId = messageId,
            conversationId = conversationId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getAllReactionIcons(request: ServerRequest): ServerResponse {
        val reactionIcons = conversationService.getAllReactionIcons()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = reactionIcons,
                )
            )
    }

    suspend fun removeMessage(request: ServerRequest): ServerResponse {
        val messageId = request.pathVariable("messageId").toLongOrNull() ?: throw InvalidParamsException("messageId")
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val removeForSelfOnly = request.queryParamOrNull("removeForSelfOnly")?.toBooleanStrictOrNull()
            ?: throw InvalidParamsException("removeForSelfOnly")
        conversationService.removeMessage(
            userId = AuthUserHolder.awaitUser().id,
            messageId = messageId,
            conversationId = conversationId,
            removeForSelfOnly = removeForSelfOnly,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>(),
            )
    }

    suspend fun getConversationAttachments(request: ServerRequest): ServerResponse {
        val conversationId = request.pathVariable("conversationId").toLongOrNull()
            ?: throw InvalidParamsException("conversationId")
        val attachmentTypes = request.queryParamOrNull("attachmentTypes").orEmpty().split(",")
            .mapNotNull {
                try {
                    Message.AttachmentType.valueOf(it.uppercase())
                } catch (err: Throwable) {
                    null
                }
            }
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: 20
        val (attachments, total) = conversationService.getConversationAttachments(
            conversationId = conversationId,
            attachmentTypes = attachmentTypes,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = attachments,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }
}