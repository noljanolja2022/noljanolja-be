package com.noljanolja.server.consumer.adapter.youtube

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.consumer.config.service.ServiceConfig
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
    serviceConfig: ServiceConfig,
) {
    companion object {
        const val COMMENT_THREAD_ENDPOINT = "/v3/commentThreads"
        const val PART_ID_SNIPPET = "snippet"
    }

    val apiKey = serviceConfig.configs.first { it.id == ServiceConfig.Config.ServiceID.YOUTUBE }.extra["apiKey"]
        ?: ""

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
        .headers {
            it.setBearerAuth(bearer)
        }
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
                println("Comment to youtube failed. VideoId: $videoId, message: ${response.error.message} with code ${response.error.code}")
                if (response.error.code == "403" &&
                    response.error.message =="The caller's YouTube account is not connected to Google+.") {
                    com.noljanolja.server.consumer.exception.Error.NoYoutubeAccountToComment
                } else
                    DefaultBadRequestException(Error(response.error.message))
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(DefaultInternalErrorException(null))
        }
        .awaitBody<YoutubeCommonResource>()
}