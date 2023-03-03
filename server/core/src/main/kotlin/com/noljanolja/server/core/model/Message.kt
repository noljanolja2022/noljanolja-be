package com.noljanolja.server.core.model

import com.noljanolja.server.core.model.CoreUser
import java.time.Instant

data class Message(
    val id: Long,
    val conversationId: Long,
    val message: String,
    val type: MessageType,
    val sender: CoreUser,
    val createdAt: Instant,
    val updatedAt: Instant,
    val attachments: List<Attachment> = listOf()
)

enum class MessageType {
    PlainText,
    Sticker,
    Gif,
    Photo,
    Document
}