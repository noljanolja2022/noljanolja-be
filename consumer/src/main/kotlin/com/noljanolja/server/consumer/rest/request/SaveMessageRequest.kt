package com.noljanolja.server.consumer.rest.request

import com.noljanolja.server.consumer.model.Message

data class SaveMessageRequest(
    val message: String,
    val type: Message.Type,
)
