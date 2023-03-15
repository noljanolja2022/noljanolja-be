package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Message
import java.time.Instant

data class CoreMessage(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val sender: CoreUser,
    val type: Type,
    val seenBy: List<String> = listOf(),
    val createdAt: Instant = Instant.now(),
) {

    enum class Type {
        PLAINTEXT,
        STICKER,
        GIF,
        PHOTO,
        DOCUMENT,
    }
}

fun CoreMessage.toConsumerMessage() = Message(
    id = id,
    conversationId = conversationId,
    message = message,
    sender = sender.toConsumerUser(),
    type = Message.Type.valueOf(type.name),
    seenBy = seenBy,
    createdAt = createdAt,
)