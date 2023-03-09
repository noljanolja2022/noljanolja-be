package com.noljanolja.server.consumer.model

import com.noljanolja.server.consumer.utils.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Conversation(
    val id: Long,
    val title: String,
    val creator: User,
    val type: Type,
    val messages: List<Message>,
    val participants: List<User>,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant = Instant.now(),
) {
    enum class Type {
        SINGLE,
        GROUP,
    }
}