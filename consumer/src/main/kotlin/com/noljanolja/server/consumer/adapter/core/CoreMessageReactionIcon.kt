package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.MessageReactionIcon

data class CoreMessageReactionIcon(
    val id: Long,
    val code: String,
    val description: String,
)

fun CoreMessageReactionIcon.toMessageReactionIcon() = MessageReactionIcon(
    id = id,
    code = code,
    description = description,
)
