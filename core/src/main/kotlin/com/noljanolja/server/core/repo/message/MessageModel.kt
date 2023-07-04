package com.noljanolja.server.core.repo.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.model.Message
import com.noljanolja.server.core.repo.user.UserModel
import com.noljanolja.server.core.repo.user.toUser
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("messages")
data class MessageModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("message")
    var message: String,

    @Column("sender_id")
    val senderId: String,

    @Column("left_participant_ids")
    var leftParticipantIds: String? = null,

    @Column("join_participant_ids")
    var joinParticipantIds: String? = null,

    @Column("conversation_id")
    val conversationId: Long,

    @Column("type")
    val type: Message.Type,

    @Column("is_deleted")
    var isDeleted: Boolean = false,

    @Column("reply_to_message_id")
    val replyToMessageId: Long? = null,

    @Column("share_message_id")
    val shareMessageId: Long? = null,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var sender: UserModel = UserModel()

    @Transient
    var seenBy: List<String> = listOf()

    @Transient
    var leftParticipants: List<UserModel> = emptyList()

    @Transient
    var joinParticipants: List<UserModel> = emptyList()

    @Transient
    var attachments: List<AttachmentModel> = listOf()

    @Transient
    var reactions: List<MessageParticipantReactionModel> = listOf()

    @Transient
    var replyToMessage: MessageModel? = null

    @Transient
    var shareMessage: MessageModel? = null
}

fun MessageModel.toMessage(objectMapper: ObjectMapper): Message = Message(
    id = id,
    conversationId = conversationId,
    message = message,
    sender = sender.toUser(objectMapper),
    leftParticipants = leftParticipants.map { it.toUser(objectMapper) },
    joinParticipants = joinParticipants.map { it.toUser(objectMapper) },
    type = type,
    seenBy = seenBy,
    createdAt = createdAt,
    attachments = attachments.map { it.toAttachment() },
    reactions = reactions.map {
        Message.Reaction(
            reactionId = it.reactionId,
            reactionCode = it.reaction.code,
            reactionDescription = it.reaction.description,
            userId = it.participantId,
            userName = it.participant.name,
        )
    },
    isDeleted = isDeleted,
    shareMessage = shareMessage?.toMessage(objectMapper),
    replyToMessage = replyToMessage?.toMessage(objectMapper),
)
