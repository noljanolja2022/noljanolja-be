package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.openai.ChatCompletionRequest
import com.noljanolja.server.admin.adapter.openai.OpenAIApi
import com.noljanolja.server.admin.adapter.youtube.YoutubeApi
import com.noljanolja.server.admin.model.PromoteVideoRequest
import com.noljanolja.server.admin.model.PromotedVideoConfig
import com.noljanolja.server.admin.model.TrackInfo
import com.noljanolja.server.admin.model.Video
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.rest.Response
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URL
import java.time.Instant

@Component
class VideoService(
    private val youtubeApi: YoutubeApi,
    private val coreApi: CoreApi,
    private val openAIApi: OpenAIApi,
    private val notificationService: NotificationService,
) {
    data class SampleComment1(
        val comment: String,
    )

    data class SampleComment2(
        val text: String,
    )

    private val logger = LoggerFactory.getLogger(VideoService::class.java)

    companion object {
        const val TOPIC_PROMOTE_VIDEO = "/topics/promote-video"
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun createVideo(youtubeUrl: String, isHighlighted: Boolean, availableFrom: Instant? = null, availableTo: Instant? = null): Video? {
        val queries = parseYoutubeUrlQuery(youtubeUrl)
        val videoId = queries.firstOrNull { it.first == "v" }?.second
            ?: throw DefaultBadRequestException(Exception("Not a valid youtube url"))
        val video = coreApi.importVideo(videoId, youtubeUrl, isHighlighted, availableFrom, availableTo)
        GlobalScope.launch {
            generateComments(videoId)
        }
        return video
    }

    suspend fun getVideoDetail(videoId: String): Video? {
        return coreApi.getVideoDetail(videoId)
    }

    suspend fun getVideo(query: String? = null, page: Int, pageSize: Int): Response<List<Video>> {
        return coreApi.getVideo(query, page, pageSize)
    }

    suspend fun getVideoAnalytics(
        page: Int,
        pageSize: Int
    ): Response<List<TrackInfo>> {
        return coreApi.getVideoAnalytics(
            page = page,
            pageSize = pageSize
        )
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

    suspend fun generateComments(
        videoId: String,
    ): List<String> {
        val snippet = youtubeApi.fetchVideoDetail(listOf(videoId)).items.firstOrNull()?.snippet ?: return emptyList()
        val description = snippet.description
        val response = openAIApi.chatCompletion(
            request = ChatCompletionRequest(
                messages = listOf(
                    ChatCompletionRequest.ChatMessage(
                        role = "system",
                        content = "Imagine you are a person commenting on a video. Try to come up with 5 different comments based on description and title of the video. The title and the description will be provided in xml in user query. Your response should be in JSON array format"
                    ),
                    ChatCompletionRequest.ChatMessage(
                        role = "user",
                        content = "<title>${snippet.title}</title>" +
                                "<description>${description.chunked(1800).first()}</description>"
                    )
                )
            )
        )
        /*
            The reason we need to try catch many times is because sometimes the AI may generate weird JSON format
            which will lead to exception when we try to decode the JSON in the wrong format
         */
        val comments = try {
            Json.decodeFromString<List<String>>(response.choices.first().message.content)
        } catch (e: Exception) {
            Json.decodeFromString<List<SampleComment1>>(response.choices.first().message.content).map { it.comment }
        } catch (e: Exception) {
            Json.decodeFromString<List<SampleComment2>>(response.choices.first().message.content).map { it.text }
        } catch (e: Exception) {
            logger.error("Failed to decode the generated comments: ${e.message}")
            emptyList()
        }
        if (comments.isNotEmpty()) {
            coreApi.upsertVideoGeneratedComments(
                comments = comments,
                videoId = videoId,
            )
        }
        return comments
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