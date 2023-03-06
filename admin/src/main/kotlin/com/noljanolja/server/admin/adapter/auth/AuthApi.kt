package com.noljanolja.server.admin.adapter.auth

import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.exception.ExternalServiceException
import com.noljanolja.server.common.rest.Response
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Component
class AuthApi(
    @Qualifier("authWebClient") private val webClient: WebClient,
) {
    companion object {
        const val USERS_ENDPOINT = "/api/v1/users"
    }

    suspend fun getUser(
        bearerToken: String,
    ): AuthUser? = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT).build()
        }
        .header(HttpHeaders.AUTHORIZATION, bearerToken)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            // TODO check error
            Mono.just(DefaultUnauthorizedException(null))
        }
        .onStatus(HttpStatus::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<AuthUser>>().data
}