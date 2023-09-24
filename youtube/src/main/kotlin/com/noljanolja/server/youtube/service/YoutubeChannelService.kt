package com.noljanolja.server.youtube.service

import com.noljanolja.server.youtube.model.YoutubeChannel
import com.noljanolja.server.youtube.model.YoutubeSearchResponse
import org.springframework.stereotype.Component

@Component
class YoutubeChannelService(
    private val youtubeApi: YoutubeApi
) {
    suspend fun fetchChannelDetail(id: String): YoutubeSearchResponse<YoutubeChannel> {
        return youtubeApi.fetchChannelDetail(id)
    }
}