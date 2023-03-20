package com.noljanolja.server.core.rest.request

data class SaveAttachmentsRequest(
    val attachments: List<Attachment> = listOf(),
) {
    data class Attachment(
        val type: String,
        val originalName: String,
        val name: String,
        val size: Long,
        val md5: String,
    )
}