package com.noljanolja.server.admin.adapter.openai

import com.noljanolja.server.admin.model.OpenAIError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class OpenAIApi(
    @Qualifier("openAIWebClient") private val webClient: WebClient,
) {
    companion object {
        const val CHAT_COMPLETION_ENDPOINT = "/v1/chat/completions"
    }

    data class ErrorResponse(
        val error: Error = Error(),
    ) {
        data class Error(
            val message: String = "",
            val type: String = "",
        )
    }

    suspend fun chatCompletion(
        request: ChatCompletionRequest,
    ): ChatCompletionResponse = webClient.post()
        .uri { uriBuilder -> uriBuilder.path(CHAT_COMPLETION_ENDPOINT).build() }
        .bodyValue(request)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<ErrorResponse>().mapNotNull { response ->
                OpenAIError.BadRequest(response.error.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(OpenAIError.ConnectionError)
        }
        .awaitBody<ChatCompletionResponse>()
}