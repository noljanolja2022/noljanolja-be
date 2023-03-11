package com.noljanolja.server.consumer.rsocket

import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.service.ConversationPubSubService
import io.rsocket.core.RSocketServer
import io.rsocket.core.Resume
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Profile
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import javax.annotation.PreDestroy


@Controller
class ConversationController(
    private val pubsubService: ConversationPubSubService,
) {
    private fun getTopic(userId: String) = "conversations-$userId"

    /**
     * Stream conversation message. Message data will be in the following format:
     *
     *   | type      | message                     | attachments                                |
     *   |-----------|-----------------------------|--------------------------------------------|
     *   | Plaintext | text content                | no                                         |
     *   | Sticker   | {stickerPack}/{stickerName} | no                                         |
     *   | Gif       | gif url                     | no                                         |
     *   | Document  | no                          | {files: [{rawFileName: uploadedFileName}]} |
     *   | Image     | image caption               | {files: [{rawFileName: uploadedFileName}]} |
     *   | Video     | no                          | {files: [{rawFileName: uploadedFileName}]} |
     */
    @MessageMapping("v1/conversations")
    suspend fun streamConversations(requester: RSocketRequester): Flow<Conversation> {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name
        requester.rsocket()?.onClose()?.doFirst {
            println("Client: $userId CONNECTED.");
        }?.doOnError { error ->
            println("Channel to client $userId CLOSED with error $error")
        }?.doFinally {
            println("Client $userId DISCONNECTED")
        }?.subscribe()
        return pubsubService.subscribe(getTopic(userId))
    }
}