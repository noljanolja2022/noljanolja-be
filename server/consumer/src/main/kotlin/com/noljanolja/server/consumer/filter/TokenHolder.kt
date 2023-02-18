package com.noljanolja.server.consumer.filter

import com.noljanolja.server.common.model.TokenData
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context

object TokenHolder {
    private val TOKEN: String = TokenHolder::class.java.name + ".TOKEN"

    fun withToken(payload: TokenData) = Context.of(TOKEN, Mono.just(payload))

    private fun getToken() = Mono.deferContextual<TokenData> { contextView ->
        if (contextView.hasKey(TOKEN)) contextView.get(TOKEN) else Mono.empty()
    }

    suspend fun awaitToken(): TokenData? = getToken().awaitFirstOrNull()
}