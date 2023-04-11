package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Message
import java.time.Instant

data class CoreMessage(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val sender: CoreUser,
    val leftParticipants: List<CoreUser> = emptyList(),
    val joinParticipants: List<CoreUser> = emptyList(),
    val type: Type,
    val seenBy: List<String> = listOf(),
    val createdAt: Instant = Instant.now(),
    val attachments: List<CoreAttachment> = listOf(),
) {

    enum class Type {
        PLAINTEXT,
        STICKER,
        GIF,
        PHOTO,
        DOCUMENT,
        EVENT_UPDATED,
        EVENT_LEFT,
        EVENT_JOINED
    }
}

fun CoreMessage.toConsumerMessage() = Message(
    id = id,
    conversationId = conversationId,
    message = message,
    sender = sender.toConsumerUser(),
    leftParticipants = leftParticipants.map { it.toConsumerUser() },
    joinParticipants = joinParticipants.map { it.toConsumerUser() },
    type = Message.Type.valueOf(type.name),
    seenBy = seenBy,
    attachments = attachments.map { it.toConsumerAttachment() },
    createdAt = createdAt,
)