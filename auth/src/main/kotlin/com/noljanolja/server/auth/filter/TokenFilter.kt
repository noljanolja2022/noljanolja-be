package com.noljanolja.server.auth.filter

import com.google.firebase.auth.FirebaseAuth
import com.noljanolja.server.common.exception.DefaultUnauthorizedException
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
        if (!token.startsWith(BEARER_PREFIX)) {
            throw DefaultUnauthorizedException(
                cause = IllegalArgumentException("Token does not start with Bearer")
            )
        }
        val firebaseToken = try {
            firebaseAuth.verifyIdToken(token.substring(BEARER_PREFIX.length))
        } catch (error: Exception) {
            throw DefaultUnauthorizedException(
                cause = error
            )
        }

        chain.filter(exchange)
            .contextWrite(TokenHolder.withToken(firebaseToken))
            .awaitFirstOrNull()
    }
}