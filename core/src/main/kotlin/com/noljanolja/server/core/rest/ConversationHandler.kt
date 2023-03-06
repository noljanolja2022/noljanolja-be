package com.noljanolja.server.core.rest

import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ConversationHandler(
    // conversation service
) {
    suspend fun getConversations(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun createConversation(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getConversationDetails(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getConversationMessages(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun saveConversationMessages(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}