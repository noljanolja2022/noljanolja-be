package com.noljanolja.server.core.repo.conversation

import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.model.ConversationType
import com.noljanolja.server.core.model.Message
import com.noljanolja.server.core.repo.attachment.toAttachment
import com.noljanolja.server.core.repo.message.MessageModel
import com.noljanolja.server.core.repo.user.UserModel
import com.noljanolja.server.core.repo.user.toUser
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("conversations")
internal data class ConversationModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("title")
    var title: String,

    @Column("type")
    var type: ConversationType,

    @Column("creator_id")
    var creatorId: UUID,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)

internal fun ConversationModel.toConversation(
    participants: List<UserModel>,
    messages: List<MessageModel>,
    creator: UserModel,
    senderInfos: List<UserModel>
): Conversation = Conversation(
    id = id,
    title = title,
    type = type,
    participants = participants.map { it.toUser() },
    messages = messages.map { message ->
        Message(
            id = message.id,
            message = message.message,
            conversationId = 0,
            type = message.type,
            sender = senderInfos.first { it.id == message.senderId }.toUser(),
            createdAt = message.createdAt,
            updatedAt = message.updatedAt,
            attachments = message.attachments.map { it.toAttachment() }
        )
    },
    creator = creator.toUser()
)

