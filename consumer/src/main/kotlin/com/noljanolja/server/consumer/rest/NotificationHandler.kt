package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.filter.AuthUserHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class NotificationHandler (
    private val coreApi: CoreApi,
){
    suspend fun getNotifications(request: ServerRequest): ServerResponse {
        val userId = AuthUserHolder.awaitUser().id
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: GiftHandler.DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: GiftHandler.DEFAULT_PAGE_SIZE
        val notifications = coreApi.getNotifications(userId, page, pageSize)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = notifications
                )
            )
    }

    suspend fun readNotification(request: ServerRequest): ServerResponse {
        val userId = AuthUserHolder.awaitUser().id
        val id = request.pathVariable("notification-id").toLongOrNull() ?: throw InvalidParamsException("notification-id")

        coreApi.readNotification(userId, id)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = null
                )
            )
    }

    suspend fun readAllNotifications(request: ServerRequest): ServerResponse {
        val userId = AuthUserHolder.awaitUser().id

        coreApi.readAllNotifications(userId)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = null
                )
            )
    }
}