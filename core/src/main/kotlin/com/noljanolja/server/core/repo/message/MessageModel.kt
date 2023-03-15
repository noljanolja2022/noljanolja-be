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
    val message: String,

    @Column("sender_id")
    val senderId: String,

    @Column("conversation_id")
    val conversationId: Long,

    @Column("type")
    val type: Message.Type,

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
}

fun MessageModel.toMessage(objectMapper: ObjectMapper) = Message(
    id = id,
    conversationId = conversationId,
    message = message,
    sender = sender.toUser(objectMapper),
    type = type,
    seenBy = seenBy,
    createdAt = createdAt,
)
