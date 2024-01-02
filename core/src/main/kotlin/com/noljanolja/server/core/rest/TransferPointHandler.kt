package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.service.TransferPointService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class TransferPointHandler(
    private val transferPointService: TransferPointService
) {
    suspend fun requestPoint(request: ServerRequest): ServerResponse {
        val fromUserId = request.queryParamOrNull("fromUserId")
            ?: throw InvalidParamsException("fromUserId")
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

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.CREATED.value(),
                    data = userTransferPoint
                )
            )
    }

    suspend fun sendPoint(request: ServerRequest): ServerResponse {
        val fromUserId = request.queryParamOrNull("fromUserId")
            ?: throw InvalidParamsException("fromUserId")
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

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.CREATED.value(),
                    data = userTransferPoint
                )
            )
    }
}