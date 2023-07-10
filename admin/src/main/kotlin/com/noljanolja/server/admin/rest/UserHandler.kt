package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.filter.AuthUserHolder
import com.noljanolja.server.admin.model.CreateUserRequest
import com.noljanolja.server.admin.model.UserActivationRequest
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.admin.service.UserService
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService,
    private val googleStorageService: GoogleStorageService,
    private val objectMapper: ObjectMapper
) {
    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        val currentUser = AuthUserHolder.awaitUser().toUser()
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = currentUser)
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
    suspend fun createAdminUser(request: ServerRequest): ServerResponse {
        val currentUser = AuthUserHolder.awaitUser()
        val payload = request.awaitBodyOrNull<CreateUserRequest>() ?: throw RequestBodyRequired
        val user = userService.createAdminUser(currentUser.bearerToken, payload)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }
    suspend fun updateUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("id")
        val payload = request.awaitBodyOrNull<UserActivationRequest>() ?: throw RequestBodyRequired
        val user = userService.updateUser(userId, payload)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }
    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("id")
        userService.deleteUser(userId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }
}