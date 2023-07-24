package com.noljanolja.server.consumer.model

import java.time.Instant

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val type: Type,
    val sender: User,
    val leftParticipants: List<User> = emptyList(),
    val joinParticipants: List<User> = emptyList(),
    val seenBy: List<String> = listOf(),
    val attachments: List<Attachment> = listOf(),
    var localId: String = "",
    val createdAt: Instant = Instant.now(),
    val reactions: List<Reaction> = emptyList(),
    val isDeleted: Boolean,
    val shareMessage: Message? = null,
    val replyToMessage: Message? = null,
    val shareVideo: Video? = null,
) {
    data class Reaction(
        val reactionId: Long,
        val reactionCode: String,
        val reactionDescription: String,
        val userId: String,
        val userName: String,
    )

    data class Attachment(
        val id: Long = 0,
        val messageId: Long = 0,
        val name: String,
        val originalName: String,
        val size: Long,
        val type: String,
        val md5: String,
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