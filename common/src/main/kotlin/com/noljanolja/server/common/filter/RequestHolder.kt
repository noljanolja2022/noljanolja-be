package com.noljanolja.server.common.filter

import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono
import reactor.util.context.Context

object RequestHolder {
    private val HEADERS_CONFIG: String = RequestHolder::class.java.name + ".HEADERS_CONFIG"

    fun withHeaders(header: HttpHeaders): Context = Context.of(HEADERS_CONFIG, Mono.just(header))

    fun getHeaders(): Mono<HttpHeaders> = Mono.deferContextual { contextView ->
        if (contextView.hasKey(HEADERS_CONFIG)) contextView.get(HEADERS_CONFIG) else Mono.empty()
    }
}