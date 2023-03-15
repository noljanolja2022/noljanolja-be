package com.noljanolja.server.core.model

import java.time.Instant

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val sender: User,
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

    enum class Status {
        SEEN,
    }
}