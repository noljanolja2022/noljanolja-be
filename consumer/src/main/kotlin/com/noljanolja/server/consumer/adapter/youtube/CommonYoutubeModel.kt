package com.noljanolja.server.consumer.adapter.youtube


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

data class YoutubeCommonResource(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: YoutubeSnippet,
)

data class YoutubeSnippet(
    val channelId: String? = null,
    val videoId: String? = null,
    val topLevelComment: TopLevelComment? = null
)