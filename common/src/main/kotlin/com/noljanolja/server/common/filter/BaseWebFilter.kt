package com.noljanolja.server.common.filter

import com.noljanolja.server.common.model.ClientInfo
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class BaseWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (exchange.request.path.toString().startsWith("/api", ignoreCase = true)) {
            val userAgentHeader = exchange.request.headers.getFirst(HttpHeaders.USER_AGENT).orEmpty()
            ClientInfo.parseString(userAgentHeader)?.let { clientInfo ->
                filterApi(exchange, chain).contextWrite(ClientInfoHolder.withClientInfo(clientInfo))
            } ?: filterApi(exchange, chain)

        } else {
            chain.filter(exchange)
        }
    }

    abstract fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void>
}
