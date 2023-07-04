package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.Message

data class SaveMessageRequest(
    val senderId: String,
    val message: String,
    val type: Message.Type,
    val shareMessageId: Long? = null,
    val replyToMessageId: Long? = null,
)