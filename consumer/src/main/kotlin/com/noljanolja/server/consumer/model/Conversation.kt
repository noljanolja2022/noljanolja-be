package com.noljanolja.server.consumer.model

import java.time.Instant

data class Conversation(
    val id: Long,
    val title: String,
    val type: ConversationType,
    val creator: User,
    var participants: List<User> = listOf(),
    val messages: List<Message> = listOf(),
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)

enum class ConversationType {
    Single,
    Group
}
