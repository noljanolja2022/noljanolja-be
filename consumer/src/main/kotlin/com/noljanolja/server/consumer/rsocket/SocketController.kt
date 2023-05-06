package com.noljanolja.server.consumer.rsocket

import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.VideoProgress
import com.noljanolja.server.consumer.service.ConversationPubSubService
import com.noljanolja.server.consumer.service.VideoPubSubService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Controller

@Controller
class SocketController(
    private val pubsubService: ConversationPubSubService,
    private val videoPubSubService: VideoPubSubService
) {

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
        return pubsubService.subscribe(ConversationPubSubService.getTopic(userId))
    }

    @MessageMapping("v1/videos")
    suspend fun listenVideoProgress(request: VideoProgress) {
        val userId = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.name
        println("Received track video request from userId: $userId with event ${request.event}")
        videoPubSubService.saveProgress(userId, request)
    }

    @MessageMapping("")
    suspend fun test(request: VideoProgress) {
        println("Received track video request for videoId: ${request.videoId}")
    }
}