package com.noljanolja.server.core.repo.message

import com.noljanolja.server.common.model.CoreUser
import com.noljanolja.server.core.model.Message
import com.noljanolja.server.core.model.MessageType
import com.noljanolja.server.core.repo.attachment.AttachmentModel
import com.noljanolja.server.core.repo.attachment.toAttachment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("messages")
data class MessageModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("conversation_id")
    var conversationId: Long,

    @Column("sender_id")
    var senderId: UUID,

    @Column("type")
    var type: MessageType,

    @Column("message")
    var message: String,

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) {
    @Transient
    var attachments: List<AttachmentModel> = listOf()
}

internal fun MessageModel.toMessage(senderInfo: CoreUser) = Message(
    id = id,
    conversationId = conversationId,
    type = type,
    message = message,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sender = CoreUser(
        id = senderId.toString(),
        name = senderInfo.name,
        pushToken = "",
        pushNotiEnabled = false,
        profileImage = senderInfo.profileImage,
        firebaseUserId = ""
    ),
    attachments = attachments.map { it.toAttachment() }
)