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
    val channelId: String,
    val channelTitle: String,
    val channelThumbnail: String,
)