package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.request.*
import com.noljanolja.server.consumer.adapter.core.response.GetUsersResponseData
import com.noljanolja.server.consumer.exception.CoreServiceError
import com.noljanolja.server.consumer.model.StickerPack
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriUtils
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
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
        const val MEDIA_ENDPOINT = "/api/v1/media"

        val coreErrorsMapping = mapOf(
            404_001 to CoreServiceError.UserNotFound
        )
    }

    suspend fun getUsers(
        friendId: String? = null,
        phoneNumber: String? = null,
        page: Int = 1,
        pageSize: Int = 100,
    ): Pair<List<CoreUser>, Pagination>? = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT).apply {
                queryParam("page", page)
                queryParam("pageSize", pageSize)
                queryParamIfPresent("friendId", Optional.ofNullable(friendId))
                queryParamIfPresent("phoneNumber", Optional.ofNullable(phoneNumber?.let { UriUtils.encode(it, StandardCharsets.UTF_8) }))
            }.build()
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
        .awaitBody<Response<GetUsersResponseData>>().data?.let {
            it.users to it.pagination
        }

    suspend fun getUserDetails(
        userId: String,
    ): CoreUser = webClient.get()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/{userId}").build(userId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                if (response.code in coreErrorsMapping) {
                    coreErrorsMapping[response.code]
                } else {
                    CoreServiceError.CoreServiceBadRequest(response.message)
                }
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreUser>>().data!!

    suspend fun upsertUser(
        user: CoreUser,
    ): CoreUser = webClient.post()
        .uri { builder ->
            builder.path(USERS_ENDPOINT).build()
        }
        .bodyValue(user)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreUser>>().data!!

    suspend fun deleteUser(userId: String) = webClient.delete()
        .uri { it.path("$USERS_ENDPOINT/$userId").build() }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Nothing>>().data

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
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreUser>>>().data!!

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
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
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
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
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

    suspend fun updateConversation(
        payload: UpdateConversationRequest,
        conversationId: Long,
    ): CoreConversation = webClient.put()
        .uri { builder ->
            builder.path(GET_CONVERSATION_DETAIL_ENDPOINT)
                .build(conversationId)
        }
        .bodyValue(payload)
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

    suspend fun updateMessageStatus(
        payload: UpdateMessageStatusRequest,
        conversationId: Long,
        messageId: Long,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$MESSAGE_ENDPOINT/{messageId}")
                .build(conversationId, messageId)
        }
        .bodyValue(payload)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Nothing>>()

    suspend fun saveAttachments(
        payload: SaveAttachmentsRequest,
        messageId: Long,
        conversationId: Long,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$MESSAGE_ENDPOINT/{messageId}/attachments")
                .build(conversationId, messageId)
        }
        .bodyValue(payload)
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

    suspend fun getAttachmentById(
        userId: String,
        conversationId: Long,
        attachmentId: Long,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$GET_CONVERSATION_DETAIL_ENDPOINT/attachments/{attachmentId}")
                .queryParam("userId", userId)
                .build(conversationId, attachmentId)
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
        .awaitBody<Response<CoreAttachment>>().data!!

    suspend fun getAllStickerPacksFromUser(
        userId: String
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/sticker-packs").build()
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
        .awaitBody<Response<List<StickerPack>>>().data

    suspend fun getStickerPack(
        stickerPackId: Long
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/sticker-packs/$stickerPackId").build()
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
        .awaitBody<Response<StickerPack>>().data
}
