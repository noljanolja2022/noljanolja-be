package com.noljanolja.server.consumer.model

import java.time.Instant

data class Message(
    val id: Long,
    val message: String,
    val sender: User,
    val type: MessageType,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class MessageType {
    Plaintext,
    Sticker,
    Gif,
    Photo,
    Document
}