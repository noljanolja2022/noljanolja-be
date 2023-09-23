package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.PromoteVideoRequest
import com.noljanolja.server.admin.model.PromotedVideoConfig
import com.noljanolja.server.admin.model.Video
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.rest.Response
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.net.URL

@Component
class VideoService(
    private val coreApi: CoreApi,
    private val notificationService: NotificationService,
) {
    companion object {
        const val TOPIC_PROMOTE_VIDEO = "/topics/promote-video"
    }

    suspend fun createVideo(youtubeUrl: String, isHighlighted: Boolean): Video? {
        val queries = parseYoutubeUrlQuery(youtubeUrl)
        val videoId = queries.firstOrNull { it.first == "v" }?.second
            ?: throw DefaultBadRequestException(Exception("Not a valid youtube url"))
        return coreApi.importVideo(videoId, youtubeUrl, isHighlighted)
    }

    suspend fun getVideoDetail(videoId: String): Video? {
        return coreApi.getVideoDetail(videoId)
    }

    suspend fun getVideo(query: String? = null, page: Int, pageSize: Int): Response<List<Video>> {
        val res = coreApi.getVideo(query, page, pageSize)
        return res
    }

    suspend fun deleteVideo(videoId: String) {
        coreApi.deleteVideo(videoId)
    }

    suspend fun getPromotedVideo(): Response<List<PromotedVideoConfig>> {
        return coreApi.getPromotedVideos()
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updatePromotedVideo(videoId: String, promoteVideoRequest: PromoteVideoRequest) {
        coreApi.updatePromotedVideo(videoId, promoteVideoRequest)
        GlobalScope.launch {
            coreApi.getVideoDetail(videoId).apply {
                notificationService.pushToTopic(
                    TOPIC_PROMOTE_VIDEO,
                    mapOf(
                        "id" to id,
                        "url" to url,
                        "title" to title,
                        "thumbnail" to thumbnail,
                        "duration" to duration,
                    )
                )
            }
        }
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