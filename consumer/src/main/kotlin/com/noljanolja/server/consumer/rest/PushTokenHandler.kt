package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class PushTokenHandler() {
    suspend fun updatePushToken(request: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}