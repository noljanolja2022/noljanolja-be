package com.noljanolja.server.consumer.rsocket

import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.service.ConversationPubSubService
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller


@Controller
class ConversationController(
    private val pubsubService: ConversationPubSubService,
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
        // TODO logic
        return pubsubService.subscribe(getTopic(0))
    }

    private fun getTopic(userId: Long) = "conversations-$userId"
}