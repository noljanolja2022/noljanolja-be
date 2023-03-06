package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.common.exception.DefaultNotFoundException
import com.noljanolja.server.common.exception.ExternalServiceException
import com.noljanolja.server.common.rest.Response
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

    suspend fun getUser(
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
}