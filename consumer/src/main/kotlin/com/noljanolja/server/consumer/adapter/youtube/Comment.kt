package com.noljanolja.server.consumer.adapter.youtube

import java.time.Instant

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

data class TopLevelCommentRequest(
    val snippet: YoutubeSnippet
)