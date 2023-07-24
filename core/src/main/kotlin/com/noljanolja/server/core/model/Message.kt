package com.noljanolja.server.core.model

import java.time.Instant

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val sender: User,
    val leftParticipants: List<User> = emptyList(),
    val joinParticipants: List<User> = emptyList(),
    val type: Type,
    val seenBy: List<String> = listOf(),
    val createdAt: Instant = Instant.now(),
    val attachments: List<Attachment> = listOf(),
    val reactions: List<Reaction> = emptyList(),
    val isDeleted: Boolean = false,
    val replyToMessage: Message? = null,
    val shareMessage: Message? = null,
    val shareVideo: Video? = null,
) {
    data class Reaction(
        val reactionId: Long,
        val reactionCode: String,
        val reactionDescription: String,
        val userId: String,
        val userName: String,
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

    enum class Status {
        SEEN,
        REMOVED,
    }
}