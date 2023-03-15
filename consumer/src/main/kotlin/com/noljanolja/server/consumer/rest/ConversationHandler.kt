package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.rest.request.CreateConversationRequest
import com.noljanolja.server.consumer.service.ConversationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConversationHandler(
    private val conversationService: ConversationService,
) {
    companion object {
        const val QUERY_PARAM_CONVERSATION_ID = "conversationId"
        const val QUERY_PARAM_MESSAGE_ID = "messageId"
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
        val payload = request.awaitBodyOrNull<CreateConversationRequest>() ?: throw RequestBodyRequired
        val conversation = with(payload) {
            conversationService.createConversation(
                userId = AuthUserHolder.awaitUser().id,
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

    suspend fun sendConversationMessages(request: ServerRequest): ServerResponse {
        val payload = request.awaitMultipartData()
        val conversationId = request.pathVariable(QUERY_PARAM_CONVERSATION_ID).toLongOrNull()
            ?: throw InvalidParamsException(QUERY_PARAM_CONVERSATION_ID)
        val message = payload.getFirst("message")?.content()?.awaitSingle()?.let {
            String(it.asInputStream().readAllBytes())
        }.orEmpty()
        val type = payload.getFirst("type")?.content()?.awaitSingle()?.let {
            Message.Type.valueOf(String(it.asInputStream().readAllBytes()))
        } ?: Message.Type.PLAINTEXT
        val data = conversationService.createMessage(
            userId = AuthUserHolder.awaitUser().id,
            message = message,
            type = type,
            conversationId = conversationId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = data,
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
}