package com.noljanolja.server.admin.filter

import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.adapter.auth.AuthUser
import com.noljanolja.server.admin.model.CoreServiceError
import com.noljanolja.server.admin.model.NoPrivilegeUser
import com.noljanolja.server.common.exception.InvalidTokenProvidedException
import com.noljanolja.server.common.exception.NoTokenProvidedException
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
class AuthFilter(
    private val authApi: AuthApi,
) : BaseWebFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private val PRIVILEGED_ROLES = listOf(AuthUser.Role.ADMIN, AuthUser.Role.STAFF)
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
        val authUser = authApi.getUser(token) ?: throw CoreServiceError.UserNotFound
        if (authUser.roles.intersect(PRIVILEGED_ROLES).isEmpty()) {
            throw NoPrivilegeUser
        }
        chain.filter(exchange)
            .contextWrite(AuthUserHolder.withUser(authUser))
            .awaitFirstOrNull()
    }
}