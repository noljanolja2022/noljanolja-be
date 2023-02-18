package com.noljanolja.server.auth.rest

import com.noljanolja.server.auth.filter.TokenHolder
import com.noljanolja.server.auth.service.FirebaseService
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.model.TokenData
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class AuthHandler(
    private val firebaseService: FirebaseService,
) {
    suspend fun getUserInfo(request: ServerRequest): ServerResponse {
        val token = TokenHolder.awaitToken()
        val user = firebaseService.getUserInfo(
            uid = token.uid,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = user,
                )
            )
    }

    suspend fun verifyToken(request: ServerRequest): ServerResponse {
        val token = TokenHolder.awaitToken()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = TokenData(
                        userId = token.uid,
                    ),
                )
            )
    }
}