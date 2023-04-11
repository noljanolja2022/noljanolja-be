package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Video
import java.time.Instant

data class CoreVideo(
    val id: String,
    val url: String,
    val publishedAt: Instant,
    val title: String,
    val thumbnail: String,
    val duration: String,
    val durationMs: Long,
    val viewCount: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val isHighlighted: Boolean,
    val comments: List<CoreVideoComment> = listOf(),
    val channel: Channel,
    val category: Category,
) {
    data class Channel(
        val id: String,
        val title: String,
        val thumbnail: String,
    )

    data class Category(
        val id: String,
        val title: String,
    )
}

fun CoreVideo.toConsumerVideo() = Video(
    id = id,
    url = url,
    publishedAt = publishedAt,
    title = title,
    thumbnail = thumbnail,
    duration = duration,
    durationMs = durationMs,
    viewCount = viewCount,
    likeCount = likeCount,
    favoriteCount = favoriteCount,
    commentCount = commentCount,
    isHighlighted = isHighlighted,
    comments = comments.map { it.toConsumerVideoComment() },
    channel = Video.Channel(
        id = channel.id,
        title = channel.title,
        thumbnail = channel.thumbnail,
    ),
    category = Video.Category(
        id = category.id,
        title = category.title,
    )
)