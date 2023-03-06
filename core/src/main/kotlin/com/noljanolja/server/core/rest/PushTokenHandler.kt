package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.DeviceType
import com.noljanolja.server.core.model.UserDevice
import com.noljanolja.server.core.rest.request.UpsertPushTokenRequest
import com.noljanolja.server.core.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class PushTokenHandler(
    private val userService: UserService,
) {
    suspend fun getPushToken(request: ServerRequest): ServerResponse {
        val userId = request.queryParamOrNull("userId").orEmpty().ifBlank {
            throw DefaultBadRequestException(cause = IllegalArgumentException("Missing userId"))
        }
        val userDevices = userService.getUserDevices(userId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = userDevices.map { it.deviceToken }
                )
            )
    }

    suspend fun upsertPushToken(request: ServerRequest): ServerResponse {
        val upsertPushTokenRequest = request.awaitBodyOrNull<UpsertPushTokenRequest>() ?: throw RequestBodyRequired
        userService.upsertUserDevice(
            UserDevice(
                id = 0,
                userId = upsertPushTokenRequest.userId,
                deviceType = DeviceType.valueOf(upsertPushTokenRequest.deviceType),
                deviceToken = upsertPushTokenRequest.deviceToken,
            )
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}