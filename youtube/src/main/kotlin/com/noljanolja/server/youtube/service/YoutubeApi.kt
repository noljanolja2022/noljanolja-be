package com.noljanolja.server.youtube.service

import com.noljanolja.server.common.exception.BaseException
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.youtube.ServiceConfig
import com.noljanolja.server.youtube.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class YoutubeApi(
    @Qualifier("youtubeWebClient") private val webClient: WebClient,
    serviceConfig: ServiceConfig,
) {
    companion object {
        const val VIDEO_ENDPOINT = "/v3/videos"
        const val PART_ID_STATISTIC = "statistics"
        const val LIKE_ENDPOINT = "/v3/videos/rate"
        const val SUBSCRIPTION = "/v3/subscriptions"
        const val VIDEO_CATEGORY_ENDPOINT = "/v3/videoCategories"
        const val VIDEO_CHANNEL_ENDPOINT = "/v3/channels"
        const val PART_ID_SNIPPET = "snippet"
        const val PART_ID_CONTENT_DETAIL = "contentDetails"
        const val COMMENT_THREAD_ENDPOINT = "/v3/commentThreads"
    }

    val apiKey = serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.YOUTUBE }.extra["apiKey"]
        ?: ""

    suspend fun fetchVideoDetail(
        ids: List<String>,
        part: List<String> = listOf(PART_ID_SNIPPET, PART_ID_STATISTIC, PART_ID_CONTENT_DETAIL)
    ) = webClient.get()
        .uri { builder ->
            builder.path(VIDEO_ENDPOINT)
                .queryParam("id", URLEncoder.encode(ids.joinToString(","), StandardCharsets.UTF_8))
                .queryParam("part", URLEncoder.encode(part.joinToString(","), StandardCharsets.UTF_8))
                .queryParam("key", apiKey)
                .build()
        }
        .retrieve()
        .awaitBody<YoutubeSearchResponse<YoutubeVideo>>()

    suspend fun fetchVideoCategory(id: String) = webClient.get()
        .uri{ builder ->
            builder.path(VIDEO_CATEGORY_ENDPOINT)
                .queryParam("id", id)
                .queryParam("part", PART_ID_SNIPPET)
                .queryParam("key", apiKey)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<YoutubeSearchResponse<YoutubeVideoCategory>>()

    suspend fun fetchChannelDetail(id: String)= webClient.get()
        .uri{ builder ->
            builder.path(VIDEO_CHANNEL_ENDPOINT)
                .queryParam("id", id)
                .queryParam("part", PART_ID_SNIPPET)
                .queryParam("key", apiKey)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<YoutubeSearchResponse<YoutubeChannel>>()

    suspend fun subscribeToChannel(channelId: String, bearer: String) = webClient.post()
        .uri { builder ->
            builder.path(SUBSCRIPTION)
                .queryParam("part", "snippet")
                .build()
        }
        .headers { it.setBearerAuth(bearer) }
        .bodyValue(
            AddSubscriptionRequest(
                AddSubscriptionRequestSnippet(
                    resourceId = YoutubeSnippet(
                        channelId = channelId,
                        kind = "youtube#channel"
                    ),
                )
            )
        )
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<YoutubeCommonResource>()

    suspend fun unsubscribeFromChannel(
        subscriptionId: String,
        bearer: String
    ) = webClient.delete()
        .uri { builder ->
            builder.path(SUBSCRIPTION)
                .queryParam("id", subscriptionId)
                .build()
        }
        .headers { it.setBearerAuth(bearer) }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<Any>()

    suspend fun rateVideo(
        videoId: String,
        bearer: String,
        rating: String,
    ) = webClient.post()
        .uri { builder ->
            builder.path(LIKE_ENDPOINT)
                .queryParam("id", videoId)
                .queryParam("rating", rating)
                .build()
        }
        .headers { it.setBearerAuth(bearer) }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBodyOrNull<Any>()

    suspend fun addToplevelComment(
        videoId: String,
        bearer: String,
        comment: String,
        part: List<String> = listOf(PART_ID_SNIPPET)
    ) = webClient.post()
        .uri { builder ->
            builder.path(COMMENT_THREAD_ENDPOINT)
                .queryParam("part", URLEncoder.encode(part.joinToString(","), StandardCharsets.UTF_8))
                .build()
        }
        .headers { it.setBearerAuth(bearer) }
        .bodyValue(
            TopLevelCommentRequest(
                YoutubeSnippet(
                    videoId = videoId,
                    topLevelComment = TopLevelComment(
                        snippet = TopLevelComment.TopLevelCommentSnippet(
                            textOriginal = comment
                        )
                    )
                )
            )
        )
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<YoutubeError>().mapNotNull { response ->
                if (response.error.message.contains("Google+")) {
                    Error.NoYoutubeAccountForAction
                }
                BaseException(response.error.code.toInt(), response.error.message, null)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<YoutubeCommonResource>()
}