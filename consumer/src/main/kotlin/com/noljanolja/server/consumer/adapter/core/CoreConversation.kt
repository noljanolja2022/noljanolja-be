package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import java.time.Instant

data class CoreConversation(
    val id: Long,
    val title: String,
    val creator: CoreUser,
    val type: Type,
    val messages: List<Message>,
    val participants: List<CoreUser>,
    val imageUrl: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {

    enum class Type {
        SINGLE,
        GROUP,
    }
}

fun CoreConversation.toConsumerConversation() = Conversation(
    id = id,
    title = title,
    creator = creator.toConsumerUser(),
    type = Conversation.Type.valueOf(type.name),
    messages = messages,
    participants = participants.map { it.toConsumerUser() },
    imageUrl = imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)