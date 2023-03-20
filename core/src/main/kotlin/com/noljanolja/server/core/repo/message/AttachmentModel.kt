package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.model.Attachment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("attachments")
data class AttachmentModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("message_id")
    val messageId: Long,

    @Column("type")
    val type: String,

    @Column("original_name")
    val originalName: String,

    @Column("name")
    val name: String,

    @Column("size")
    val size: Long,

    @Column("md5")
    val md5: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)

fun AttachmentModel.toAttachment() = Attachment(
    id = id,
    name = name,
    originalName = originalName,
    size = size,
    type = type,
    md5 = md5,
    messageId = messageId,
)