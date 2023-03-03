package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.admin.rest.CoreServiceError
import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.request.UpsertUserRequest
import com.noljanolja.server.common.rest.EmptyResponse
import com.noljanolja.server.common.rest.Response
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono


@Component
class CoreApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val GET_USER_INFO_ENDPOINT = "/api/v1/users/{userId}"
        const val UPSERT_USER_ENDPOINT = "/api/v1/users"

        val errorsMapper = mapOf<Int, CoreServiceError>(
            404_001 to CoreServiceError.UserNotFound
        )
    }

    suspend fun getUserInfo(
        userId: String,
    ): Response<CoreUser> = webClient.get()
        .uri { builder ->
            builder.path(GET_USER_INFO_ENDPOINT)
                .build(userId)
        }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            it.bodyToMono<EmptyResponse>().mapNotNull { response ->
                if (errorsMapper.containsKey(response.code)) {
                    errorsMapper[response.code]
                } else {
                    CoreServiceError.CoreServiceBadRequest(response.message)
                }
            }
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody()

    suspend fun upsertUser(
        userRequest: UpsertUserRequest
    ): Response<CoreUser> = webClient.post()
        .uri { builder ->
            builder.path(UPSERT_USER_ENDPOINT)
                .build()
        }
        .bodyValue(userRequest)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            it.bodyToMono<EmptyResponse>().mapNotNull { response ->
                if (errorsMapper.containsKey(response.code)) {
                    errorsMapper[response.code]
                } else {
                    CoreServiceError.CoreServiceBadRequest(response.message)
                }
            }
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody()
}