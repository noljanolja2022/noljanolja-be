package com.noljanolja.server.admin.adapter.auth

import com.noljanolja.server.admin.model.CoreServiceError
import com.noljanolja.server.common.rest.Response
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
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
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<AuthUser>>().data
}