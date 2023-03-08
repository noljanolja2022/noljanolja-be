package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.common.exception.DefaultNotFoundException
import com.noljanolja.server.common.exception.ExternalServiceException
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.request.UpsertPushTokenRequest
import com.noljanolja.server.consumer.adapter.core.request.UpsertUserContactsRequest
import com.noljanolja.server.consumer.adapter.core.request.CreateConversationRequest
import com.noljanolja.server.consumer.adapter.core.request.SaveMessageRequest
import com.noljanolja.server.consumer.adapter.core.response.GetUsersResponseData
import com.noljanolja.server.consumer.exception.CoreServiceError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.*

@Component
class CoreApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val USERS_ENDPOINT = "/api/v1/users"
        const val PUSH_TOKENS_ENDPOINT = "/api/v1/push-tokens"
        const val CONVERSATION_ENDPOINT = "/api/v1/conversations"
        const val GET_CONVERSATION_DETAIL_ENDPOINT = "/api/v1/conversations/{conversationId}"
        const val MESSAGE_ENDPOINT = "/api/v1/conversations/{conversationId}/messages"
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
        .onStatus(HttpStatusCode::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
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
        .onStatus(HttpStatusCode::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
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
        .bodyValue(UpsertUserContactsRequest(localContacts))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<Nothing>>()

    suspend fun getPushToken(
        userId: String,
    ): List<String> = webClient.get()
        .uri { builder ->
            builder.path(PUSH_TOKENS_ENDPOINT)
                .queryParam("userId", userId)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<List<String>>>().data.orEmpty()

    suspend fun upsertPushToken(
        userId: String,
        deviceType: String,
        deviceToken: String,
    ) = webClient.post()
        .uri { builder ->
            builder.path(PUSH_TOKENS_ENDPOINT).build()
        }
        .bodyValue(UpsertPushTokenRequest(userId, deviceToken, deviceType))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            // TODO check error: 401, 403, 404
            Mono.just(DefaultNotFoundException(null))
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            // TODO check error
            Mono.just(ExternalServiceException(null))
        }
        .awaitBody<Response<Nothing>>()

    suspend fun createConversation(
        request: CreateConversationRequest
    ): CoreConversation = webClient.post()
        .uri { builder -> builder.path(CONVERSATION_ENDPOINT).build() }
        .bodyValue(request)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreConversation>>().data!!

    suspend fun saveMessage(
        request: SaveMessageRequest,
        conversationId: Long,
    ): CoreMessage = webClient.post()
        .uri { builder ->
            builder
                .path(MESSAGE_ENDPOINT)
                .build(conversationId)
        }
        .bodyValue(request)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreMessage>>().data!!

    suspend fun getUserConversations(
        userId: String,
        messageLimit: Long = 1,
        senderLimit: Long = 4,
    ): List<CoreConversation> = webClient.get()
        .uri { builder ->
            builder.path(CONVERSATION_ENDPOINT)
                .queryParam("userId", userId)
                .queryParam("messageLimit", messageLimit)
                .queryParam("senderLimit", senderLimit)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreConversation>>>().data!!

    suspend fun getConversationDetail(
        userId: String,
        conversationId: Long,
    ): CoreConversation = webClient.get()
        .uri { builder ->
            builder.path(GET_CONVERSATION_DETAIL_ENDPOINT)
                .queryParam("userId", userId)
                .build(conversationId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreConversation>>().data!!

    suspend fun getConversationMessages(
        userId: String,
        conversationId: Long,
        beforeMessageId: Long?,
        afterMessageId: Long?
    ): List<CoreMessage> = webClient.get()
        .uri { builder ->
            builder.path(MESSAGE_ENDPOINT)
                .queryParam("userId", userId)
                .queryParam("conversationId", conversationId)
                .queryParamIfPresent("afterMessageId", Optional.ofNullable(afterMessageId))
                .queryParamIfPresent("beforeMessageId", Optional.ofNullable(beforeMessageId))
                .build(conversationId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreMessage>>>().data!!
}
