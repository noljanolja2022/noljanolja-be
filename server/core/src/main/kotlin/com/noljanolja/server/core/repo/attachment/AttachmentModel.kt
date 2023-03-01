package com.noljanolja.server.core.repo.attachment

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
    var id: Long = 0,

    @Column("message_id")
    var messageId: Long,

    @Column("type")
    var type: String,

    @Column("original_name")
    var originalName: String,

    @Column("name")
    var name: String,

    @Column("size")
    var size: Long,

    @Column("md5")
    var md5: String = "",

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
)

internal fun AttachmentModel.toAttachment() = Attachment(
    id = id,
    type = type,
    originalName = originalName,
    name = name,
    size = size,
    md5 = md5,
    updatedAt = updatedAt,
    createdAt = createdAt
)