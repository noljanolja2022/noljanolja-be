package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.service.UserDS
import com.noljanolja.server.common.model.request.UpsertUserRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class CoreHandler(
    private val userDS: UserDS,
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
            body = Response(
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
            body = Response(
                data = user,
            )
        )
    }

    suspend fun upsertUser(request: ServerRequest): ServerResponse {
        val upsertUserRequest = request.awaitBodyOrNull<UpsertUserRequest>() ?: throw RequestBodyRequired
        val user = userDS.upsertUser(upsertUserRequest)
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = user,
            )
        )
    }
}