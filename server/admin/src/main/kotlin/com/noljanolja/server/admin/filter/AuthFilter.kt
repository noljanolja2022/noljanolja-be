package com.noljanolja.server.admin.filter

import com.noljanolja.server.common.rest.BaseWebFilter
import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.rest.AdminError
import com.noljanolja.server.core.model.AuthUser
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
        private val PRIVILEGED_ROLES = listOf(AuthUser.CustomClaim.Role.ADMIN, AuthUser.CustomClaim.Role.STAFF)
    }

    override fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty().trim()
        if (!token.startsWith(BEARER_PREFIX)) throw AdminError.UnauthorizedError
        return mono {
            val tokenData = authApi.verifyToken(token).data
            if (tokenData.roles.intersect(PRIVILEGED_ROLES).isEmpty()) throw AdminError.ForbiddenError
            chain.filter(exchange)
                .contextWrite(
                    TokenHolder.withToken(
                        tokenData.apply {
                            bearerToken = token
                        }
                    )
                )
                .awaitFirstOrNull()
        }
    }
}