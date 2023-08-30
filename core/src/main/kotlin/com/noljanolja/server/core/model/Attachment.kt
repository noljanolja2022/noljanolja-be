package com.noljanolja.server.core.model

import com.noljanolja.server.core.repo.message.AttachmentType
import java.time.Instant

data class Attachment(
    val id: Long = 0,
    val messageId: Long,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
    val attachmentType: AttachmentType,
    val previewImage: String,
    val createdAt: Instant,
    val durationMs: Long,
)