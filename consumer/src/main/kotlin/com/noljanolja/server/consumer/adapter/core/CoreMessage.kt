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
    val reactions: List<Reaction> = emptyList(),
) {
    data class Reaction(
        val reactionId: Long,
        val reactionCode: String,
        val reactionDescription: String,
        val userName: String,
        val userId: String,
    )

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
    reactions = reactions.map {
        Message.Reaction(
            reactionId = it.reactionId,
            reactionCode = it.reactionCode,
            reactionDescription = it.reactionDescription,
            userName = it.userName,
            userId = it.userId,
        )
    }
)