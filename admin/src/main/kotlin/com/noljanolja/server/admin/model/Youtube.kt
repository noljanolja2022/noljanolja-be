package com.noljanolja.server.admin.model

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
    val contentDetails: ContentDetail?,
    val statistics: Statistic?
) {
    data class Statistic(
        val viewCount: String = "0",
        val likeCount: String = "0",
        val favoriteCount: String = "0",
        val commentCount: String = "0"
    )

    data class ContentDetail(
        val duration: String ,
        val dimension: String?,
        val definition: String?,
        val caption: Boolean = false,
        val licensedContent: Boolean= false,
        val contentRating: Any,
        val projection: String?,
    )

    data class Snippet(
        val publishedAt: Instant,
        val channelId: String,
        val title: String,
        val description: String,
        val thumbnails: YoutubeThumbnail,
        val channelTitle: String,
        val tags: List<String>,
        val categoryId: String,
        val liveBroadcastContent: String,
        val localized: YoutubeLocalizedData,
        val defaultAudioLanguage: String
    )
}

data class YoutubeChannel(
    val kind: String,
    val etag: String= "",
    val id: String,
    val snippet: Snippet,
) {
    data class Snippet(
        val title: String= "",
        val description: String= "",
        val customUrl: String = "",
        val publishedAt: Instant,
        val thumbnails: YoutubeThumbnail,
        val defaultLanguage: String?,
        val localized: YoutubeLocalizedData?,
        val country: String?
    )
}

data class YoutubeVideoCategory(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: Snippet,
) {
    data class Snippet(
        val channelId: String,
        val title: String,
        val assignable: Boolean
    )
}

data class YoutubeThumbnail(
    val default: ThumbnailDetail,
    val medium: ThumbnailDetail?,
    val high: ThumbnailDetail?,
    val standard: ThumbnailDetail?,
    val maxres: ThumbnailDetail?
) {
    data class ThumbnailDetail(
        val url: String,
        val width: Int,
        val height: Int,
    )

    fun getDefaultThumbnail() : String {
        return standard?.url ?: high?.url ?: default.url
    }
}

data class YoutubeLocalizedData(
    val title: String,
    val description: String
)