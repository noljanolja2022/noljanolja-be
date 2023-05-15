package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.filter.AuthUserHolder
import com.noljanolja.server.admin.model.CreateUserRequest
import com.noljanolja.server.admin.service.UserService
import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService,
) {
    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        val currentUserId = AuthUserHolder.awaitUser().id
        val user = userService.getCurrentUser(currentUserId) ?: throw DefaultUnauthorizedException(null)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun createUser(request: ServerRequest): ServerResponse {
        val currentUser = AuthUserHolder.awaitUser()
        val payload = request.awaitBodyOrNull<CreateUserRequest>() ?: throw RequestBodyRequired
        val user = userService.createUser(currentUser.bearerToken, payload)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 }
            ?: 10
        val phoneNumber = request.queryParamOrNull("phoneNumber")
        val searchUserRes = userService.getUsers(page, pageSize, phoneNumber)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = searchUserRes.data, pagination = searchUserRes.pagination)
            )
    }
}