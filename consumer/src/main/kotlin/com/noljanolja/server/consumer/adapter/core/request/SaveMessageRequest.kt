package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.adapter.core.CoreMessage

data class SaveMessageRequest(
    val senderId: String,
    val message: String,
    val type: CoreMessage.Type,
)
