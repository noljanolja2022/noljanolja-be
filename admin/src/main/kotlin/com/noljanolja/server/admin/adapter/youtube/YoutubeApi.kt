package com.noljanolja.server.admin.adapter.youtube

import com.noljanolja.server.admin.config.service.ServiceConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class YoutubeApi(
    @Qualifier("youtubeWebClient") private val webClient: WebClient,
    serviceConfig: ServiceConfig,
) {
    companion object {
        const val VIDEO_ENDPOINT = "/v3/videos"
        const val PART_ID_SNIPPET = "snippet"
    }

    val apiKey =
        serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.YOUTUBE }.extra["apiKey"].orEmpty()

    data class YoutubeErrorResponse(
        val error: String = "",
    )

    suspend fun fetchVideoDetail(
        ids: List<String>,
        part: List<String> = listOf(PART_ID_SNIPPET)
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
}