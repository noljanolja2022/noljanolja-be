package com.noljanolja.server.consumer.adapter.core


import com.noljanolja.server.common.model.CoreUser
import com.noljanolja.server.common.model.request.UpsertUserRequest
import com.noljanolja.server.common.rest.EmptyResponse
import com.noljanolja.server.common.rest.GetAnnouncementsResponse
import com.noljanolja.server.common.rest.GetUserResponse
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.rest.CoreServiceError
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
        const val GET_ANNOUNCEMENTS_END_POINT = "/api/v1/announcements"

        val errorsMapper = mapOf<Int, CoreServiceError>(
            404_001 to CoreServiceError.UserNotFound
        )
    }

    suspend fun getUserInfo(
        userId: String,
    ): GetUserResponse = webClient.get()
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
    ): GetUserResponse = webClient.post()
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

    suspend fun getAnnouncements(
        page: Long,
        pageSize: Long,
    ): GetAnnouncementsResponse = webClient.get()
        .uri { builder ->
            builder.path(GET_ANNOUNCEMENTS_END_POINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build()
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
}