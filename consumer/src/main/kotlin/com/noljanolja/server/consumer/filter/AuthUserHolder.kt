package com.noljanolja.server.consumer.filter

import com.noljanolja.server.consumer.adapter.auth.AuthUser
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context

object AuthUserHolder {
    private val USER: String = AuthUserHolder::class.java.name + ".USER"

    fun withUser(user: AuthUser) = Context.of(USER, Mono.just(user))

    suspend fun awaitUser(): AuthUser? = getUser().awaitFirstOrNull()

    private fun getUser() = Mono.deferContextual<AuthUser> { contextView ->
        if (contextView.hasKey(USER)) contextView.get(USER) else Mono.empty()
    }
}