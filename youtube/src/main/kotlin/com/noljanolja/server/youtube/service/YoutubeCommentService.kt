package com.noljanolja.server.youtube.service

import org.springframework.stereotype.Component

@Component
class YoutubeCommentService(
    private val youtubeApi: YoutubeApi
) {
    suspend fun postComment(token: String, videoId: String, comment: String) {
        youtubeApi.addToplevelComment(videoId, token, comment)
    }
}