package com.noljanolja.server.common.filter

import com.noljanolja.server.common.model.ClientInfo
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import reactor.util.context.Context

object ClientInfoHolder {
    private val CLIENT_INFO: String = ClientInfoHolder::class.java.name + ".CLIENT_INFO"

    fun withClientInfo(clientInfo: ClientInfo?) = Context.of(CLIENT_INFO, Mono.justOrEmpty(clientInfo))

    suspend fun awaitClientInfo(): ClientInfo? = getClientInfo().awaitFirstOrNull()

    private fun getClientInfo() = Mono.deferContextual<ClientInfo> { contextView ->
        if (contextView.hasKey(CLIENT_INFO)) contextView.get(CLIENT_INFO) else Mono.empty()
    }
}