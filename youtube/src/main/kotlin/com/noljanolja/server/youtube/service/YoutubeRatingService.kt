package com.noljanolja.server.youtube.service

import org.springframework.stereotype.Component

@Component
class YoutubeRatingService(
    private val youtubeApi: YoutubeApi
) {
    suspend fun rateVideo(token: String, videoId: String, action: String) {
        youtubeApi.ratingVideo(videoId, token, action)
    }
}