package com.noljanolja.server.admin.adapter.youtube

import com.noljanolja.server.admin.config.service.ServiceConfig
import com.noljanolja.server.admin.model.*
import com.noljanolja.server.common.rest.Response
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class YoutubeApi(
    @Qualifier("youtubeWebClient") private val webClient: WebClient,
    private val serviceConfig: ServiceConfig,
) {
    companion object {
        const val VIDEO_ENDPOINT = "/v3/videos"
        const val VIDEO_CATEGORY_ENDPOINT = "/v3/videoCategories"
        const val VIDEO_CHANNEL_ENDPOINT = "/v3/channels"
        const val PART_ID_SNIPPET = "snippet"
        const val PART_ID_CONTENT_DETAIL = "contentDetails"
        const val PART_ID_STATISTIC = "statistics"

    }

    val apiKey = serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.YOUTUBE }.extra["apiKey"]
        ?: ""

    suspend fun fetchVideoDetail(
        id: String,
        part: List<String> = listOf(PART_ID_SNIPPET, PART_ID_CONTENT_DETAIL, PART_ID_STATISTIC)
    ) = webClient.get()
        .uri { builder ->
            builder.path(VIDEO_ENDPOINT)
                .queryParam("id", id)
                .queryParam("part", URLEncoder.encode(part.joinToString(","), StandardCharsets.UTF_8))
                .queryParam("key", apiKey)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
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
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
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
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<YoutubeSearchResponse<YoutubeChannel>>()
}