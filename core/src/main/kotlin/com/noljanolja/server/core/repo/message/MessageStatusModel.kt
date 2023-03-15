package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.model.Message
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("message_status")
data class MessageStatusModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("message_id")
    val messageId: Long,

    @Column("user_id")
    val userId: String,

    @Column("status")
    val status: Message.Status,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
