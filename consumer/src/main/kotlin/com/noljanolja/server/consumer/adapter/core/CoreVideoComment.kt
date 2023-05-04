package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.VideoComment
import java.time.Instant

data class CoreVideoComment(
    val id: Long,
    val comment: String,
    val commenter: VideoCommenter,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    data class VideoCommenter(
        val name: String,
        val avatar: String,
    )
}

fun CoreVideoComment.toConsumerVideoComment() = VideoComment(
    id = id,
    comment = comment,
    commenter = VideoComment.VideoCommenter(
        name = commenter.name,
        avatar = commenter.avatar,
    ),
    createdAt = createdAt,
    updatedAt = updatedAt,
)