package com.noljanolja.server.auth.rest

import com.noljanolja.server.auth.filter.TokenHolder
import com.noljanolja.server.auth.service.FirebaseService
import com.noljanolja.server.core.model.AuthUser
import com.noljanolja.server.core.model.TokenData
import com.noljanolja.server.common.util.enumByNameIgnoreCase
import com.noljanolja.server.core.model.response.GetAuthUserResponse
import com.noljanolja.server.core.model.response.GetTokenDataResponse
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
                GetAuthUserResponse(
                    data = user,
                )
            )
    }

    suspend fun verifyToken(request: ServerRequest): ServerResponse {
        val token = TokenHolder.awaitToken()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                GetTokenDataResponse(
                    data = TokenData(
                        userId = token.uid,
                        roles = listOf(
                            enumByNameIgnoreCase(
                                token.claims[FirebaseService.CUSTOM_CLAIM_KEY_ROLE].toString(),
                                AuthUser.CustomClaim.Role.CONSUMER
                            )
                        )
                    ),
                )
            )
    }
}