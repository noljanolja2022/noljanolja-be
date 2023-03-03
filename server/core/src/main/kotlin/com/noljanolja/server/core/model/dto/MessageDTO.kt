package com.noljanolja.server.core.model.dto

import com.noljanolja.server.common.util.InstantSerializer
import com.noljanolja.server.core.model.MessageType
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MessageDTO(
    val id: Long,
    val message: String,
    val sender: UserDTO,
    val type: MessageType,
    val attachments: List<AttachmentDTO> = listOf(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant,
) {
    companion object {
//        fun fromMessage(message: Message): MessageDTO = MessageDTO(
//            id = message.id,
//            message = message.message,
//            type = message.type,
//            sender = UserDTO.fromUser(
//                user = message.sender
//            ),
//            createdAt = message.createdAt,
//            updatedAt = message.updatedAt,
//            attachments = message.attachments.map { AttachmentDTO.fromAttachment(it) }
//        )
    }
}