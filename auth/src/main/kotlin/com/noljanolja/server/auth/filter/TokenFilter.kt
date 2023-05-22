package com.noljanolja.server.auth.filter

import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.exception.InvalidTokenProvidedException
import com.noljanolja.server.common.exception.NoTokenProvidedException
import com.noljanolja.server.common.exception.TokenExpiredException
import com.noljanolja.server.common.filter.BaseWebFilter
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(-1)
class TokenFilter(
    private val firebaseAuth: FirebaseAuth,
) : BaseWebFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun filterApi(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> = mono {
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty().trim()
        if (token.isEmpty()) {
            throw NoTokenProvidedException()
        }
        if (!token.startsWith(BEARER_PREFIX)) {
            throw InvalidTokenProvidedException()
        }
        val firebaseToken = try {
            firebaseAuth.verifyIdToken(token.substring(BEARER_PREFIX.length))
        } catch (error: FirebaseAuthException) {
            when (error.authErrorCode) {
                AuthErrorCode.EXPIRED_ID_TOKEN -> throw TokenExpiredException(Error("Token is expired, please use a new token"))

                else -> throw DefaultUnauthorizedException(
                    cause = error
                )
            }
        }

        chain.filter(exchange)
            .contextWrite(TokenHolder.withToken(firebaseToken))
            .awaitFirstOrNull()
    }
}