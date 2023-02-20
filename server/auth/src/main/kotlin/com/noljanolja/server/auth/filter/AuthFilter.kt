package com.noljanolja.server.auth.filter

import com.google.firebase.auth.FirebaseAuth
import com.noljanolja.server.auth.config.FirebaseAuthConfig
import com.noljanolja.server.common.exception.FirebaseException
import com.noljanolja.server.common.rest.BaseWebFilter
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import org.springframework.http.HttpHeaders

@Component
@Order(-1)
class AuthFilter(
    private val firebaseAuth: FirebaseAuth,
) : BaseWebFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty().trim()
        return mono {
            if (!token.startsWith(BEARER_PREFIX)) throw FirebaseException.FailedToVerifyToken(null)
            val firebaseToken = try {
                firebaseAuth.verifyIdToken(token.substring(BEARER_PREFIX.length))
            } catch (e: Exception) {
                throw FirebaseException.FailedToVerifyToken(e)
            }
            chain.filter(exchange)
                .contextWrite(TokenHolder.withToken(firebaseToken))
                .awaitFirstOrNull()
        }
    }
}