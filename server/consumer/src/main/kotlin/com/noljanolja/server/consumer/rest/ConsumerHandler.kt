package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.util.TokenHolder
import com.noljanolja.server.consumer.model.UpdateUserRequest
import com.noljanolja.server.consumer.model.response.GetAnnouncementsResponse
import com.noljanolja.server.consumer.model.response.GetUserResponse
import com.noljanolja.server.consumer.service.AnnouncementService
import com.noljanolja.server.consumer.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class ConsumerHandler(
    private val userService: UserService,
    private val announcementService: AnnouncementService,
) {
    companion object {
        const val DEFAULT_PAGE = 1L
        const val DEFAULT_PAGE_SIZE = 10L
    }

    suspend fun getMyInfo(request: ServerRequest): ServerResponse {
        val tokenData = TokenHolder.awaitToken() ?: throw ConsumerError.UnauthorizedError
        val user = userService.getMyInfo(tokenData)
        return ServerResponse.ok().bodyValueAndAwait(
            GetUserResponse(
                data = user
            )
        )
    }

    suspend fun updateCurrentUserInfo(request: ServerRequest) : ServerResponse {
        val tokenData = TokenHolder.awaitToken() ?: throw ConsumerError.UnauthorizedError
        val requestBody = request.awaitBodyOrNull<UpdateUserRequest>() ?: throw RequestBodyRequired
        val updatedUser = userService.updateUser(tokenData, requestBody)
        return ServerResponse.ok().bodyValueAndAwait(
            GetUserResponse(
                data = updatedUser
            )
        )
    }

    suspend fun getAnnouncements(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (announcements, count) = announcementService.getAnnouncements(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok().bodyValueAndAwait(
            GetAnnouncementsResponse(
                data = announcements,
                paging = Paging(
                    totalRecords = count,
                    page = page,
                    pageSize = pageSize,
                )
            )
        )
    }
}