package com.noljanolja.server.core.model

import java.time.Instant

data class Conversation(
    val id: Long,
    val title: String,
    val creator: User,
    val type: Type,
    val messages: List<Message>,
    val participants: List<User>,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {

    enum class Type {
        SINGLE,
        GROUP,
    }
}