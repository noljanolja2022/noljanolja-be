package com.noljanolja.server.auth.filter

import com.google.firebase.auth.FirebaseToken
import kotlinx.coroutines.reactive.awaitFirst
import reactor.core.publisher.Mono
import reactor.util.context.Context

object TokenHolder {
    private val TOKEN: String = TokenHolder::class.java.name + ".TOKEN"

    fun withToken(payload: FirebaseToken) = Context.of(TOKEN, Mono.just(payload))

    private fun getToken() = Mono.deferContextual<FirebaseToken> { contextView ->
        if (contextView.hasKey(TOKEN)) contextView.get(TOKEN) else Mono.empty()
    }

    suspend fun awaitToken(): FirebaseToken = getToken().awaitFirst()
}