package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.repo.message.AttachmentType

data class SaveAttachmentsRequest(
    val attachments: List<Attachment> = listOf(),
) {
    data class Attachment(
        val type: String,
        val originalName: String,
        val name: String,
        val size: Long,
        val md5: String,
        val previewImage: String,
        val attachmentType: AttachmentType,
        val durationMs: Long,
    )
}