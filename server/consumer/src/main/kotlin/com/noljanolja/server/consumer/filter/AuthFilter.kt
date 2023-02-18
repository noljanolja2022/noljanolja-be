package com.noljanolja.server.consumer.filter

import com.noljanolja.server.common.exception.FirebaseException
import com.noljanolja.server.common.rest.BaseWebFilter
import com.noljanolja.server.consumer.adapter.auth.AuthApi
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
    private val authApi: AuthApi,
) : BaseWebFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty().trim()
        if (token.startsWith(BEARER_PREFIX)) {
            return mono {
                val tokenData = authApi.verifyToken(token).data
                chain.filter(exchange)
                    .contextWrite(TokenHolder.withToken(tokenData.apply {
                        bearerToken = token
                    }))
                    .awaitFirstOrNull()
            }
        }
        return chain.filter(exchange)
    }
}