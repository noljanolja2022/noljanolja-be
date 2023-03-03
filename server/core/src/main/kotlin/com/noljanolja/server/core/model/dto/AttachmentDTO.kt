package com.noljanolja.server.core.model.dto

import com.noljanolja.server.core.model.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentDTO(
    val id: Long = 0,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
) {
    companion object {
        fun fromAttachment(attachment: Attachment) = with(attachment) {
            AttachmentDTO(
                id = id,
                type = type,
                originalName = originalName,
                name = name,
                size = size,
                md5 = md5
            )
        }
    }
}