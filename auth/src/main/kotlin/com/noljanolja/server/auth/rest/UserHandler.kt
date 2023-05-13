package com.noljanolja.server.auth.rest

import com.noljanolja.server.auth.filter.TokenHolder
import com.noljanolja.server.auth.model.CreateUserRequest
import com.noljanolja.server.auth.model.User
import com.noljanolja.server.auth.service.UserService
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService,
) {
    suspend fun getUser(request: ServerRequest): ServerResponse {
        val token = TokenHolder.awaitToken()
        val user = userService.getUser(token.uid)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = user))
    }

    suspend fun verifyToken(request: ServerRequest): ServerResponse {
        val user = with(TokenHolder.awaitToken()) {
            User(id = uid)
        }
        return ServerResponse.ok().bodyValueAndAwait(
            Response(data = user)
        )
    }

    suspend fun createUser(request: ServerRequest) : ServerResponse {
        val payload = request.awaitBodyOrNull<CreateUserRequest>() ?: throw RequestBodyRequired
        val createdUser = userService.createUser(payload.userName, payload.password)
        userService.setPermission(createdUser.id, payload.role)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(data = createdUser)
        )
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val token = TokenHolder.awaitToken()
        userService.deleteUser(token.uid)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }
}