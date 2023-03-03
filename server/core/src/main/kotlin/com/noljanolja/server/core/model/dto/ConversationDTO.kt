package com.noljanolja.server.core.model.dto

import com.noljanolja.server.common.util.InstantSerializer
import com.noljanolja.server.core.model.ConversationType
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ConversationDTO(
    val id: Long = 0,
    val title: String,
    val type: ConversationType,
    val creator: UserDTO,
    var participants: List<UserDTO> = listOf(),
    val messages: List<MessageDTO> = listOf(),
    @Serializable(with = InstantSerializer::class)
    var createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    var updatedAt: Instant = Instant.now(),
) {
//    companion object {
//        fun fromConversation(conversation: Conversation): ConversationDTO = with(conversation) {
//            ConversationDTO(
//                id = id,
//                title = title,
//                type = type,
//                creator = UserDTO.fromUser(creator),
//                participants = participants.map { UserDTO.fromUser(it) },
//                messages = messages.map{ MessageDTO.fromMessage(it) }
//            )
//        }
//    }
}