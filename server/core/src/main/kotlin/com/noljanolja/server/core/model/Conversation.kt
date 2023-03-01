package com.noljanolja.server.core.model

import com.noljanolja.server.common.model.CoreUser
import java.time.Instant

data class Conversation(
    var id: Long = 0,
    var title: String,
    var type: ConversationType,
    var participants: List<CoreUser> = listOf(),
    var messages: List<Message> = listOf(),
    var creator: CoreUser,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)

enum class ConversationType {
    Single,
    Group
}