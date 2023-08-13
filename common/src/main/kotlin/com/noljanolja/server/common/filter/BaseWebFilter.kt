package com.noljanolja.server.common.filter

import com.noljanolja.server.common.model.ClientInfo
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class BaseWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.path.toString().startsWith("/api")) {
            val userAgentHeader = exchange.request.headers.getFirst(HttpHeaders.USER_AGENT).orEmpty()
            return filterApi(exchange, chain)
                .contextWrite(ClientInfoHolder.withClientInfo(ClientInfo.parseString(userAgentHeader)))
                .contextWrite(RequestHolder.withHeaders(exchange.request.headers))
        }
        return chain.filter(exchange)
    }

    abstract fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void>
}
