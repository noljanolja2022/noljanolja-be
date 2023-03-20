package com.noljanolja.server.consumer.utils

fun getAttachmentPath(
    conversationId: Long,
    attachmentName: String,
) = "conversations/${conversationId}/attachments/$attachmentName"