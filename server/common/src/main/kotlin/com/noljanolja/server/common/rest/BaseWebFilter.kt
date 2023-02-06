package com.noljanolja.server.common.rest

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class BaseWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.path.toString().startsWith("/api", ignoreCase = true)) {
            return filterApi(exchange, chain)
        }
        return chain.filter(exchange)
    }

    abstract fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void>
}