package com.noljanolja.server.core.repo.conversation

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.model.Conversation
import com.noljanolja.server.core.repo.message.MessageModel
import com.noljanolja.server.core.repo.message.toMessage
import com.noljanolja.server.core.repo.user.UserModel
import com.noljanolja.server.core.repo.user.toUser
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("conversations")
data class ConversationModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("title")
    var title: String = "",

    @Column("type")
    val type: Conversation.Type,

    @Column("creator_id")
    val creatorId: String,

    @Column("admin_id")
    var adminId: String,

    @Column("image_url")
    var imageUrl: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var messages: List<MessageModel> = listOf()

    @Transient
    var participants: List<UserModel> = listOf()

    @Transient
    var creator: UserModel = UserModel()

    @Transient
    var admin: UserModel = UserModel()
}

fun ConversationModel.toConversation(objectMapper: ObjectMapper) = Conversation(
    id = id,
    title = title,
    creator = creator.toUser(objectMapper),
    admin = admin.toUser(objectMapper),
    type = type,
    messages = messages.map { it.toMessage(objectMapper) },
    participants = participants.map { it.toUser(objectMapper) },
    createdAt = createdAt,
    updatedAt = updatedAt,
    imageUrl = imageUrl,
)
