package com.noljanolja.server.common.rest

import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets


class ServerRequestLoggingDecorator(
    private val exchange: ServerWebExchange,
) : ServerHttpRequestDecorator(exchange.request) {
    companion object {
        const val REQUEST_BODY_ATTR: String = "RequestBody"
    }

    private val logger = LoggerFactory.getLogger(ServerRequestLoggingDecorator::class.java)
    private val requestPath = exchange.request.uri

    override fun getBody(): Flux<DataBuffer> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        return super.getBody()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext { dataBuffer ->
                try {
                    Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                } catch (e: IOException) {
                    logger.error("Logging server request at $requestPath --> Cannot read data buffer", e)
                }
            }
            .doOnComplete {
                byteArrayOutputStream.use {
                    val requestBody = String(it.toByteArray(), StandardCharsets.UTF_8)
                    exchange.attributes[REQUEST_BODY_ATTR] = requestBody
                    logger.debug("Logging server request at $requestPath with body: $requestBody")
                }
            }
            .doOnError { error ->
                byteArrayOutputStream.use {
                    logger.error("Logging server request at $requestPath --> Cannot get body from request", error)
                }
            }
    }
}
