package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.common.exception.DefaultNotFoundException
import com.noljanolja.server.common.exception.ExternalServiceException
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.request.UpdateUserContactsRequest
import com.noljanolja.server.consumer.adapter.core.response.GetUsersResponseData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Component
class CoreApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val USERS_ENDPOINT = "/api/v1/users"
    }

    suspend fun getUsers(
        friendId: String,
        page: Int,
        pageSize: Int,
    ): Pair<List<CoreUser>, Pagination>? = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParam("friendId", friendId)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatus::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<GetUsersResponseData>>().data?.let {
            it.users to it.pagination
        }

    suspend fun getUserDetails(
        userId: String,
    ): CoreUser? = webClient.get()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/{userId}").build(userId)
        }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatus::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<CoreUser>>().data

    suspend fun upsertUserContacts(
        userId: String,
        localContacts: List<CoreLocalContact>,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/{userId}/contacts").build(userId)
        }
        .bodyValue(UpdateUserContactsRequest(localContacts))
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatus::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<Nothing>>()
}
