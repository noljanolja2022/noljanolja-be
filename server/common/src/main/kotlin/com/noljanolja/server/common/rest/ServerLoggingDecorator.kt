package com.noljanolja.server.common.rest

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator

class ServerLoggingDecorator(
    private val exchange: ServerWebExchange,
) : ServerWebExchangeDecorator(exchange) {
    override fun getRequest(): ServerHttpRequest = ServerRequestLoggingDecorator(exchange)
    override fun getResponse(): ServerHttpResponse = ServerResponseLoggingDecorator(exchange)
}