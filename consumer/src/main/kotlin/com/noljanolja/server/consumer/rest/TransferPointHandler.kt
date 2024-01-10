package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.service.NotificationService
import com.noljanolja.server.consumer.service.TransferPointService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class TransferPointHandler(
    private val transferPointService: TransferPointService,
    private val notificationService: NotificationService,
    private val coreApi: CoreApi,
) {
    suspend fun requestPoint(request: ServerRequest): ServerResponse {
        val fromUserId = AuthUserHolder.awaitUser().id
        val toUserId = request.queryParamOrNull("toUserId")
            ?: throw InvalidParamsException("toUserId")
        val points = request.queryParamOrNull("points")
            ?.toLongOrNull() ?.takeIf { it > 0 }
            ?: throw InvalidParamsException("points")

        val userTransferPoint = transferPointService.requestPoint(
            fromUserId = fromUserId,
            toUserId = toUserId,
            points = points
        )

        notificationService.pushNotification(
            userId = toUserId,
            title = "${AuthUserHolder.awaitUser().name} requests you for $points points",
            body = "${AuthUserHolder.awaitUser().name} requests you for $points points",
            image = AuthUserHolder.awaitUser().avatar,
            data = mapOf(
                "user_id" to fromUserId,
                "name" to "${AuthUserHolder.awaitUser().name}",
                "points" to "$points"
            )
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.CREATED.value(),
                    data = userTransferPoint
                )
            )
    }

    suspend fun sendPoint(request: ServerRequest): ServerResponse {
        val fromUserId = AuthUserHolder.awaitUser().id
        val toUserId = request.queryParamOrNull("toUserId")
            ?: throw InvalidParamsException("toUserId")
        val points = request.queryParamOrNull("points")
            ?.toLongOrNull() ?.takeIf { it > 0 }
            ?: throw InvalidParamsException("points")

        val userTransferPoint = transferPointService.sendPoint(
            fromUserId = fromUserId,
            toUserId = toUserId,
            points = points
        )

        notificationService.pushNotification(
            userId = toUserId,
            title = "${AuthUserHolder.awaitUser().name} sends you $points points",
            body = "${AuthUserHolder.awaitUser().name} sends you $points points",
            image = AuthUserHolder.awaitUser().avatar,
            data = mapOf(
                "user_id" to fromUserId,
                "name" to "${AuthUserHolder.awaitUser().name}",
                "points" to "$points"
            )
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.CREATED.value(),
                    data = userTransferPoint
                )
            )
    }
}