package com.noljanolja.server.consumer.model

import com.noljanolja.server.consumer.utils.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val message: String = "",
    val type: Type,
    val sender: User,
    @Serializable(with = InstantSerializer::class)
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