package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.core.CoreCreateVideoRequest
import com.noljanolja.server.admin.adapter.youtube.YoutubeApi
import com.noljanolja.server.admin.model.Video
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import java.net.URL

@Component
class VideoService(
    private val coreApi: CoreApi,
    private val youtubeApi: YoutubeApi
) {
    suspend fun createVideo(youtubeUrl: String, isHighlighted: Boolean): Video? {
        val queries = parseYoutubeUrlQuery(youtubeUrl)
        val videoId = queries.firstOrNull { it.first == "v" }?.second
            ?: throw DefaultBadRequestException(Exception("Not a valid youtube url"))
        val youtubeVideo = youtubeApi.fetchVideoDetail(videoId).items.firstOrNull()
            ?: throw DefaultInternalErrorException(Exception("Unable to retrieve youtube video"))
        val youtubeCategory = youtubeApi.fetchVideoCategory(youtubeVideo.snippet.categoryId).items.firstOrNull()
            ?: throw DefaultInternalErrorException(Exception("Unable to retrieve category of the video"))
        val youtubeChannel = youtubeApi.fetchChannelDetail(youtubeVideo.snippet.channelId).items.firstOrNull()
            ?: throw DefaultInternalErrorException(Exception("Unable to retrieve channel of the video"))
        val req = CoreCreateVideoRequest.fromYoutubeVideo(youtubeUrl, youtubeVideo, youtubeChannel, youtubeCategory, isHighlighted)
        return coreApi.createVideo(req)
    }

    suspend fun getVideo(query: String? = null, page: Int, pageSize: Int): Response<List<Video>> {
        val res = coreApi.getVideo(query, page, pageSize)
        return res
    }

    suspend fun deleteVideo(videoId: String) {
        coreApi.deleteVideo(videoId)
    }

    private fun parseYoutubeUrlQuery(url: String): List<Pair<String, String>> {
        val uri = URL(url)
        return uri.query.split("&").map {
            val parts = it.split("=")
            val name = parts.firstOrNull() ?: ""
            val value = parts.drop(1).firstOrNull() ?: ""
            Pair(name, value)
        }
    }
}