package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.service.ConversationService
import com.noljanolja.server.common.rest.Response
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ConversationHandler(
    private val conversationService: ConversationService
) {
    suspend fun getConversationAnalytics(request: ServerRequest): ServerResponse {
        val conversationAnalytics = conversationService.getConversationAnalytics()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = conversationAnalytics
                )
            )
    }
}