package com.noljanolja.server.youtube.model

data class YoutubeVideoCategory(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: Snippet,
) {
    data class Snippet(
        val channelId: String,
        val title: String,
        val assignable: Boolean = false
    )
}