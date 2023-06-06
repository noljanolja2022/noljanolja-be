package com.noljanolja.server.consumer.model

import java.time.Instant

data class Conversation(
    val id: Long,
    val title: String,
    val creator: SimpleUser,
    val admin: SimpleUser,
    val type: Type,
    var messages: List<Message>,
    var participants: List<SimpleUser>,
    val imageUrl: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {
    enum class Type {
        SINGLE,
        GROUP,
    }
}