package com.noljanolja.server.consumer.model

import java.time.Instant

data class Conversation(
    val id: Long,
    val title: String,
    val creator: User,
    val type: Type,
    val messages: List<Message>,
    val participants: List<User>,
    val imageUrl: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {
    enum class Type {
        SINGLE,
        GROUP,
    }
}