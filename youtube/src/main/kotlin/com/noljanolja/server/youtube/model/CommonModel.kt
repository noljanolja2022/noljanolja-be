package com.noljanolja.server.youtube.model

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
    val statistics: Statistic
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
        val thumbnails: YoutubeThumbnail,
        val channelTitle: String,
        val tags: List<String> = emptyList(),
        val categoryId: String,
        val liveBroadcastContent: String?,
        val localized: YoutubeLocalizedData?,
        val defaultAudioLanguage: String?
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

    fun getDefaultThumbnail(): String {
        return standard?.url ?: high?.url ?: default.url
    }
}

data class YoutubeLocalizedData(
    val title: String,
    val description: String?
)

data class TopLevelComment(
    val snippet: TopLevelCommentSnippet,
    val kind : String? = null,
    val etag: String ? = null,
    val id: String ? = null,
) {
    data class TopLevelCommentSnippet(
        val textOriginal: String,
        val textDisplay: String? = null,
        val channelId: String? = null,
        val videoId: String? = null,
        val authorDisplayName: String? = null,
        val authorProfileImageUrl: String? = null,
        val authorChannelUrl: String? = null,
//        val authorChannelId: String,
        val canRate: Boolean = false,
        val viewerRating: String? = null,
        val likeCount: Int = 0,
        val publishedAt: Instant = Instant.now(),
        val updatedAt: Instant = Instant.now()
    )
}

data class YoutubeCommonResource(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: YoutubeSnippet,
)

data class YoutubeSnippet(
    val kind: String? = null,
    val channelId: String? = null,
    val videoId: String? = null,
    val topLevelComment: TopLevelComment? = null
)

data class YoutubeError(
    val error: YoutubeErrorDetail
) {
    data class YoutubeErrorDetail(
        val code: String,
        val message: String = "",
        val errors: List<YoutubeErrorCause> = emptyList()
    )

    data class YoutubeErrorCause(
        val message: String = "",
        val domain: String = "",
        val reason: String = "",
        val location: String = "",
        val locationType: String = ""
    )
}

data class AddSubscriptionRequest(
    val snippet:AddSubscriptionRequestSnippet
)

data class AddSubscriptionRequestSnippet(
    val resourceId: YoutubeSnippet
)

data class TopLevelCommentRequest(
    val snippet: YoutubeSnippet
)