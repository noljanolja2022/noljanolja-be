package com.noljanolja.server.core.model

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
    val statistics: Statistic
) {
    data class Statistic(
        val viewCount: String = "0",
        val likeCount: String = "0",
        val commentCount: String = "0"
    )
}