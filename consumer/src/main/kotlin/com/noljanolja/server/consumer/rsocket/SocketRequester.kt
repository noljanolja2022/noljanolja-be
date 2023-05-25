package com.noljanolja.server.consumer.rsocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.sendAndAwait

@Configuration
class SocketRequester(
    private val coreRSocketRequester: RSocketRequester,
) {
    suspend fun emitUserWatchVideo(
        userVideoProgress: UserVideoProgress,
    ) {
        coreRSocketRequester
            .route("watch-video")
            .data(userVideoProgress)
            .sendAndAwait()
    }

    suspend fun emitUserSendChatMessage(
        userSendChatMessage: UserSendChatMessage,
    ) {
        coreRSocketRequester
            .route("send-message")
            .data(userSendChatMessage)
            .sendAndAwait()
    }
}