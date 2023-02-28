package com.noljanolja.server.consumer.adapter.auth

import com.noljanolja.server.common.model.AuthUser
import com.noljanolja.server.common.model.TokenData
import com.noljanolja.server.common.rest.EmptyResponse
import com.noljanolja.server.common.rest.GetAuthUserResponse
import com.noljanolja.server.common.rest.GetTokenDataResponse
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.rest.AuthServiceError
import com.noljanolja.server.consumer.rest.ConsumerError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
        const val GET_USER_INFO_ENDPOINT = "/api/v1/oauth/userinfo"
        const val VERIFY_TOKEN_ENDPOINT = "/api/v1/oauth/verify"
    }

    suspend fun getUserInfo(
        userToken: String? = null,
    ): GetAuthUserResponse = webClient.get()
        .uri { builder ->
            builder.path(GET_USER_INFO_ENDPOINT)
                .build()
        }
        .apply { userToken?.let { header(HttpHeaders.AUTHORIZATION, it) } }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            it.bodyToMono<EmptyResponse>().map { response ->
                if (it.statusCode() == HttpStatus.UNAUTHORIZED) {
                    ConsumerError.UnauthorizedError
                } else {
                    AuthServiceError.AuthServiceBadRequest(response.message)
                }
            }
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(AuthServiceError.AuthServiceInternalError)
        }
        .awaitBody()

    suspend fun verifyToken(
        bearerToken: String,
    ): GetTokenDataResponse = webClient.get()
        .uri { builder ->
            builder.path(VERIFY_TOKEN_ENDPOINT)
                .build()
        }
        .apply { header(HttpHeaders.AUTHORIZATION, bearerToken) }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            it.bodyToMono<EmptyResponse>().map { response ->
                if (it.statusCode() == HttpStatus.UNAUTHORIZED) {
                    ConsumerError.UnauthorizedError
                } else {
                    AuthServiceError.AuthServiceBadRequest(response.message)
                }
            }
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(AuthServiceError.AuthServiceInternalError)
        }
        .awaitBody()
}