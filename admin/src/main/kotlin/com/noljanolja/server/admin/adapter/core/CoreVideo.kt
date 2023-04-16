package com.noljanolja.server.admin.adapter.core

import com.noljanolja.server.admin.model.YoutubeChannel
import com.noljanolja.server.admin.model.YoutubeVideo
import com.noljanolja.server.admin.model.YoutubeVideoCategory
import java.time.Duration
import java.time.Instant

data class CoreCreateVideoRequest(
    val id: String,
    val url: String,
    val publishedAt: Instant,
    val title: String,
    val thumbnail: String,
    val duration: String,
    val durationMs: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val isHighlighted: Boolean = false,
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

    companion object {
        fun fromYoutubeVideo(
            url: String,
            videoData: YoutubeVideo,
            channelData: YoutubeChannel,
            categoryData: YoutubeVideoCategory,
            isHighlighted: Boolean
        ) :CoreCreateVideoRequest {
            val duration = Duration.parse(videoData.contentDetails?.duration)
            return CoreCreateVideoRequest(
                id = videoData.id,
                url = url,
                publishedAt = videoData.snippet.publishedAt,
                title = videoData.snippet.title,
                thumbnail = videoData.snippet.thumbnails.getDefaultThumbnail(),
                duration = duration.toString(),
                durationMs = duration.toMillis(),
                likeCount = videoData.statistics?.likeCount?.toLong() ?: 0,
                favoriteCount = videoData.statistics?.favoriteCount?.toLong()?: 0,
                commentCount = videoData.statistics?.commentCount?.toLong()?: 0,
                channel = Channel(
                    id = videoData.snippet.channelId,
                    title = channelData.snippet.title,
                    thumbnail = channelData.snippet.thumbnails.getDefaultThumbnail()
                ),
                category = Category(
                    id = videoData.snippet.categoryId,
                    title = categoryData.snippet.title
                ),
                isHighlighted = isHighlighted
            )
        }
    }
}