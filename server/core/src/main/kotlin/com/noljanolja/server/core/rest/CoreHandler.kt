package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.core.model.request.CreateAnnouncementRequest
import com.noljanolja.server.core.service.UserDS
import com.noljanolja.server.core.model.request.UpsertUserRequest
import com.noljanolja.server.common.rest.*
import com.noljanolja.server.common.validator.isUUID
import com.noljanolja.server.core.model.response.GetAnnouncementsResponse
import com.noljanolja.server.core.model.response.GetUserResponse
import com.noljanolja.server.core.model.response.GetUsersResponse
import com.noljanolja.server.core.service.AnnouncementDS
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class CoreHandler(
    private val userDS: UserDS,
    private val announcementDS: AnnouncementDS,
) {
    companion object {
        const val DEFAULT_PAGE = 1L
        const val DEFAULT_PAGE_SIZE = 10L
    }

    suspend fun getUsersInfo(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (users, totalRecords) = userDS.getUsers(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok().bodyValueAndAwait(
            body = GetUsersResponse(
                data = users,
                paging = Paging(
                    totalRecords = totalRecords,
                    page = page,
                    pageSize = pageSize,
                )
            )
        )
    }

    suspend fun getUserInfo(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val user = userDS.getUserByFirebaseUserId(userId) ?: throw UserNotFound
        return ServerResponse.ok().bodyValueAndAwait(
            body = GetUserResponse(
                data = user,
            )
        )
    }

    suspend fun upsertUser(request: ServerRequest): ServerResponse {
        val upsertUserRequest = request.awaitBodyOrNull<UpsertUserRequest>() ?: throw RequestBodyRequired
        val user = userDS.upsertUser(upsertUserRequest)
        return ServerResponse.ok().bodyValueAndAwait(
            body = GetUserResponse(
                data = user,
            )
        )
    }

    suspend fun getAnnouncements(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (announcements, totalRecords) = announcementDS.getAnnouncements(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok().bodyValueAndAwait(
            body = GetAnnouncementsResponse(
                data = announcements,
                paging = Paging(
                    totalRecords = totalRecords,
                    page = page,
                    pageSize = pageSize,
                )
            )
        )
    }

    suspend fun createAnnouncement(request: ServerRequest): ServerResponse {
        val createAnnouncementRequest = request.awaitBodyOrNull<CreateAnnouncementRequest>()
            ?: throw RequestBodyRequired
        announcementDS.createAnnouncement(createAnnouncementRequest)
        return ServerResponse.ok().bodyValueAndAwait(
            body = EmptyResponse()
        )
    }

    suspend fun deleteAnnouncement(request: ServerRequest): ServerResponse {
        val announcementId = request.pathVariable("announcementId").takeIf { it.isUUID() }
            ?: throw InvalidParamsException("announcementId")
        announcementDS.deleteAnnouncement(announcementId)
        return ServerResponse.ok().bodyValueAndAwait(
            body = EmptyResponse()
        )
    }
}