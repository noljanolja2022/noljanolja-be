package com.noljanolja.server.admin.adapter.youtube

import java.time.Instant

data class YoutubeSearchResponse<T>(
    val kind: String,
    val etag: String,
    val items: List<T>,
    val pageInfo: PageInfo?,
    val nextPageToken: String?,
    val prevPageToken: String?
) {
    data class PageInfo(
        val totalResults: Int,
        val resultsPerPage: Int
    )
}

data class YoutubeVideo(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: Snippet,
    val contentDetails: ContentDetail? = null,
    val statistics: Statistic = Statistic(),
) {
    data class Statistic(
        val viewCount: String = "0",
        val likeCount: String = "0",
        val favoriteCount: String = "0",
        val commentCount: String = "0"
    )

    data class ContentDetail(
        val duration: String,
        val dimension: String?,
        val definition: String?,
        val caption: Boolean = false,
        val licensedContent: Boolean = false,
        val contentRating: Any?,
        val projection: String?,
    )

    data class Snippet(
        val publishedAt: Instant,
        val channelId: String,
        val title: String,
        val description: String,
        val channelTitle: String,
        val tags: List<String> = emptyList(),
        val categoryId: String,
        val liveBroadcastContent: String?,
        val defaultAudioLanguage: String?
    )
}