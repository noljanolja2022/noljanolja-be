package com.noljanolja.server.common.rest

import org.slf4j.LoggerFactory
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class BaseLogFilter : BaseWebFilter() {
    private val logger = LoggerFactory.getLogger(BaseLogFilter::class.java)

    override fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestPath = exchange.request.uri
        logger.debug("Logging server request at $requestPath with method: ${exchange.request.method}")
        return chain.filter(ServerLoggingDecorator(exchange))
            .doOnError {
                logger.error("Logging server response at $requestPath --> An error has occurred ", it)
            }
    }
}