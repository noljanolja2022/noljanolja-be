package com.noljanolja.server.youtube.service

import com.noljanolja.server.youtube.model.YoutubeSearchResponse
import com.noljanolja.server.youtube.model.YoutubeVideo
import org.springframework.stereotype.Component

@Component
class YoutubeVideoService(
    private val youtubeApi: YoutubeApi
) {
//    @Transactional
    suspend fun fetchVideoDetail(videoIds: List<String>): YoutubeSearchResponse<YoutubeVideo> {
        return youtubeApi.fetchVideoDetail(videoIds)
    }
}