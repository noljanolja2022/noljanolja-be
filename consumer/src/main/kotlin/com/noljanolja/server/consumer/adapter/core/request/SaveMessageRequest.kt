package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.adapter.core.CoreMessage

data class SaveMessageRequest(
    val senderId: String,
    val message: String,
    val type: CoreMessage.Type,
    val replyToMessageId: Long? = null,
    val shareMessageId: Long? = null,
    val shareVideoId: String? = null,
    var conversationIds: List<Long> = emptyList(),
)
