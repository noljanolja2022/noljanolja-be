package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService,
) {

    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        val currentUserId = AuthUserHolder.awaitUser()?.id ?: throw DefaultUnauthorizedException(null)
        val user = userService.getCurrentUser(currentUserId) ?: throw DefaultUnauthorizedException(null)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun updateCurrentUser(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun uploadCurrentUserData(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun syncCurrentUserContact(request: ServerRequest): ServerResponse {
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }
}