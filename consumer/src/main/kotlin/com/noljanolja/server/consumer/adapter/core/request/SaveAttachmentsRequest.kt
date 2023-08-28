package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.model.Message

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
        val attachmentType: Message.AttachmentType,
        val durationMs: Long,
    )
}