package com.noljanolja.server.core.rest

import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class PushTokenHandler(
    // notification service ???
) {
    suspend fun updatePushToken(request: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}