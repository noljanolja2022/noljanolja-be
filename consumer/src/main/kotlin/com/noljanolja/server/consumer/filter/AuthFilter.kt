package com.noljanolja.server.consumer.filter

import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.filter.BaseWebFilter
import com.noljanolja.server.consumer.adapter.auth.AuthApi
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
class AuthFilter(
    private val authApi: AuthApi,
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
        val authUser = authApi.verifyToken(token).apply { bearerToken = token }
        chain.filter(exchange)
            .contextWrite(AuthUserHolder.withUser(authUser))
            .awaitFirstOrNull()
    }
}