package com.noljanolja.server.core.model

import java.time.Instant

data class Video(
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
    val availableFrom: Instant?,
    val comments: List<VideoComment> = listOf(),
    val channel: Channel,
    val category: Category,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
    val isLiked: Boolean? = null
) {

    data class Category(
        val id: String,
        val title: String,
    )
}

data class VideoLogTransaction(
    val id: String,
    val url: String,
    val title: String,
    val thumbnail: String,
    val progressPercentage: Double,
    val durationMs: Long
)