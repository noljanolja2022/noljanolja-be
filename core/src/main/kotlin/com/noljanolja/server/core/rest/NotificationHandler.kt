package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.CreateNotificationRequest
import com.noljanolja.server.core.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class NotificationHandler (
    private val notificationService: NotificationService
){
    suspend fun create(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateNotificationRequest>() ?: throw RequestBodyRequired

        notificationService.createNotification(
            userId = payload.userId,
            type = payload.type,
            title = payload.title,
            body = payload.body,
            image = payload.image,
            data = payload.data,
            isRead = payload.isRead
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.CREATED.value(),
                    data = null
                )
            )
    }

    suspend fun getNotifications(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: GiftHandler.DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: GiftHandler.DEFAULT_PAGE_SIZE
        val userId = request.queryParam("userId").orElseThrow { InvalidParamsException("userId") }
        val notifications = notificationService.getNotifications(userId, page, pageSize)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = notifications
                )
            )
    }

    suspend fun readNotification(request: ServerRequest): ServerResponse {
        val userId = request.queryParam("userId").orElseThrow { InvalidParamsException("userId") }
        val id = request.pathVariable("notification-id").toLongOrNull() ?: throw InvalidParamsException("notification-id")

        notificationService.readNotification(userId, id)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = null
                )
            )
    }

    suspend fun readAllNotifications(request: ServerRequest): ServerResponse {
        val userId = request.queryParam("userId").orElseThrow { InvalidParamsException("userId") }

        notificationService.readAllNotifications(userId)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = null
                )
            )
    }
}