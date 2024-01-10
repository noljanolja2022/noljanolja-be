package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.filter.ClientInfoHolder
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.rest.request.UpdatePushTokenRequest
import com.noljanolja.server.consumer.rest.request.UpdatePushTokenLegacyRequest
import com.noljanolja.server.consumer.service.NotificationService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class PushTokenHandler(
    private val notificationService: NotificationService,
) {
    suspend fun updatePushToken(request: ServerRequest): ServerResponse {
        val currentUserId = AuthUserHolder.awaitUser().id
        val clientInfo = ClientInfoHolder.awaitClientInfo() ?: throw DefaultBadRequestException(
            cause = IllegalArgumentException("Invalid User-Agent")
        )
        val updatePushTokenRequest = request.awaitBodyOrNull<UpdatePushTokenRequest>() ?: throw RequestBodyRequired
        notificationService.upsertPushToken(currentUserId, updatePushTokenRequest.deviceToken, clientInfo.type)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}
