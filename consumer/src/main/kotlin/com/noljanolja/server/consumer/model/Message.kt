package com.noljanolja.server.consumer.model

import java.time.Instant

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val type: Type,
    val sender: User,
    val seenBy : List<String> = listOf(),
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