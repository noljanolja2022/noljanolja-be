package com.noljanolja.server.youtube.model

import java.time.Instant

data class YoutubeChannel(
    val kind: String,
    val etag: String = "",
    val id: String,
    val snippet: Snippet,
) {
    data class Snippet(
        val title: String = "",
        val description: String = "",
        val customUrl: String = "",
        val publishedAt: Instant,
        val thumbnails: YoutubeThumbnail,
        val defaultLanguage: String?,
        val localized: YoutubeLocalizedData?,
        val country: String?
    )
}