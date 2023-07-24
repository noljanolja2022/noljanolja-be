package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Conversation
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.model.SimpleUser
import java.time.Instant

data class CoreConversation(
    val id: Long,
    val title: String,
    val creator: SimpleUser,
    val admin: SimpleUser,
    val type: Type,
    var messages: List<Message>,
    val participants: List<SimpleUser>,
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
    creator = creator,
    admin = admin,
    type = Conversation.Type.valueOf(type.name),
    messages = messages,
    participants = participants,
    imageUrl = imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)