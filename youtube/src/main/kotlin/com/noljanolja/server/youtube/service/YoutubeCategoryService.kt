package com.noljanolja.server.youtube.service

import com.noljanolja.server.youtube.model.YoutubeSearchResponse
import com.noljanolja.server.youtube.model.YoutubeVideoCategory
import org.springframework.stereotype.Component

@Component
class YoutubeCategoryService(
    private val youtubeApi: YoutubeApi
) {
    suspend fun fetchCategory(id: String): YoutubeSearchResponse<YoutubeVideoCategory> {
        return youtubeApi.fetchVideoCategory(id)
    }
}