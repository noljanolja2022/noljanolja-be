package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Message

data class CoreAttachment(
    val id: Long = 0,
    val messageId: Long = 0,
    val name: String,
    val originalName: String,
    val size: Long,
    val type: String,
    val md5: String,
)

fun CoreAttachment.toConsumerAttachment() = Message.Attachment(
    id = id,
    messageId = messageId,
    name = name,
    originalName = originalName,
    size = size,
    type = type,
    md5 = md5,
)