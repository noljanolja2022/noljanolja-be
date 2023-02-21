package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.filter.TokenHolder
import com.noljanolja.server.admin.service.UserService
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class AdminHandler(
    private val userService: UserService,
) {
    suspend fun getMyInfo(request: ServerRequest): ServerResponse {
        val tokenData = TokenHolder.awaitToken() ?: throw AdminError.UnauthorizedError
        val user = userService.getMyInfo(tokenData)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = user
            )
        )
    }
}