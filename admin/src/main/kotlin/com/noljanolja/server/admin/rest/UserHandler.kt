package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.filter.AuthUserHolder
import com.noljanolja.server.admin.service.UserService
import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.rest.Response
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
}