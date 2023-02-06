package com.noljanolja.server.common.rest

import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

class ServerResponseLoggingDecorator(
    private val exchange: ServerWebExchange,
) : ServerHttpResponseDecorator(exchange.response) {
    private val logger = LoggerFactory.getLogger(ServerResponseLoggingDecorator::class.java)
    private val requestPath = exchange.request.uri

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        return super.writeWith(
            Flux.from(body)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext { dataBuffer ->
                    try {
                        Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                    } catch (e: IOException) {
                        logger.error("Logging server response at $requestPath --> Cannot read data buffer", e)
                    }
                }
                .doOnComplete {
                    byteArrayOutputStream.use {
                        val responseBody = String(it.toByteArray(), StandardCharsets.UTF_8)
                        logger.debug("Logging server response at $requestPath with body: $responseBody")
                    }
                }
                .doOnError { error ->
                    byteArrayOutputStream.use {
                        logger.error("Logging server response at $requestPath --> Cannot get body from request", error)
                    }
                }
        )
    }
}