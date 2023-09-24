package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.request.*
import com.noljanolja.server.consumer.exception.CoreServiceError
import com.noljanolja.server.consumer.model.Message
import com.noljanolja.server.consumer.model.SimpleUser
import com.noljanolja.server.consumer.model.StickerPack
import com.noljanolja.server.consumer.rest.request.AddFriendRequest
import com.noljanolja.server.consumer.rest.request.AssignReferralRequest
import com.noljanolja.server.consumer.rest.request.CoreUpdateAdminOfConversationReq
import com.noljanolja.server.consumer.rest.request.CoreUpdateMemberOfConversationReq
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriUtils
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
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
        const val LOYALTY_ENDPOINT = "/api/v1/loyalty"
        const val REWARD_ENDPOINT = "/api/v1/reward"
        const val GIFT_ENDPOINT = "/api/v1/gifts"
        const val BANNER_ENDPOINT = "/api/v1/banners"
        const val COIN_EXCHANGE_ENDPOINT = "/api/v1/coin-exchange"

        val coreErrorsMapping = mapOf(
            404_001 to CoreServiceError.UserNotFound
        )
    }

    suspend fun getUsers(
        friendId: String? = null,
        phoneNumber: String? = null,
        page: Int = 1,
        pageSize: Int = 100,
    ): Pair<List<SimpleUser>, Pagination>? = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT).apply {
                queryParam("page", page)
                queryParam("pageSize", pageSize)
                queryParamIfPresent("friendId", Optional.ofNullable(friendId))
                queryParamIfPresent(
                    "phoneNumber",
                    Optional.ofNullable(phoneNumber?.let { UriUtils.encode(it, StandardCharsets.UTF_8) })
                )
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
        .awaitBody<Response<List<SimpleUser>>>().let {
            Pair(it.data!!, it.pagination!!)
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

    suspend fun addFriend(
        userId: String,
        friend: AddFriendRequest,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/{userId}/contacts/invite").build(userId)
        }
        .bodyValue(friend)
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
        request: CreateConversationRequest,
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
        messageLimit: Int = 20,
        messageId: Long? = null,
    ): CoreConversation = webClient.get()
        .uri { builder ->
            builder.path(GET_CONVERSATION_DETAIL_ENDPOINT)
                .queryParam("userId", userId)
                .queryParam("messageLimit", messageLimit)
                .queryParamIfPresent("messageId", Optional.ofNullable(messageId))
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
        afterMessageId: Long?,
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
        attachmentId: Long,
    ) = webClient.get()
        .uri { builder ->
            builder.path("${CONVERSATION_ENDPOINT}/attachments/{attachmentId}")
                .build(attachmentId)
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
        userId: String,
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
        stickerPackId: Long,
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

    suspend fun addMemberToConversation(
        conversationId: Long,
        payload: CoreUpdateMemberOfConversationReq,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$CONVERSATION_ENDPOINT/$conversationId/participants").build()
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
        .awaitBody<Response<List<String>>>().data

    suspend fun removeMemberFromConversation(
        conversationId: Long,
        payload: CoreUpdateMemberOfConversationReq,
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$CONVERSATION_ENDPOINT/$conversationId/participants")
                .queryParam("userId", payload.userId)
                .queryParam("participantIds", payload.participantIds.joinToString(","))
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
        .awaitBody<Response<List<String>>>().data

    suspend fun setAdminToConversation(
        conversationId: Long,
        payload: CoreUpdateAdminOfConversationReq,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$CONVERSATION_ENDPOINT/$conversationId/admin").build()
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
        .awaitBody<Response<String>>().data

    suspend fun getChannelDetail(
        channelId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/channels/{channelId}").build(channelId)
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
        .awaitBody<Response<CoreChannel>>()

    suspend fun subscribeToChannel(
        youtubeToken: String,
        channelId: String, userId: String, isSubscribing: Boolean
    ) = webClient.post()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/channels/{channelId}/subscribe").build(channelId)
        }
        .bodyValue(CoreSubscribeChannelRequest(youtubeToken, isSubscribing, userId))
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

    suspend fun likeVideo(
        videoId: String,
        payload: CoreLikeVideoRequest,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/{videoId}/likes").build(videoId)
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

    suspend fun postComment(
        videoId: String,
        payload: PostCommentRequest,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/{videoId}/comments").build(videoId)
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
        .awaitBody<Response<CoreVideoComment>>().data!!

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        query: String? = null,
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos")
                .queryParamIfPresent("isHighlighted", Optional.ofNullable(isHighlighted))
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("query", Optional.ofNullable(query))
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
        .awaitBody<Response<List<CoreVideo>>>().let {
            Pair(it.data!!, it.pagination!!.total)
        }

    suspend fun getVideos(
        videoIds: List<String>,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/watching")
                .queryParam("videoIds", videoIds.joinToString(","))
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
        .awaitBody<Response<List<CoreVideo>>>()

    suspend fun getVideoDetails(
        videoId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/{videoId}")
                .build(videoId)
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
        .awaitBody<Response<CoreVideo>>().data!!

    suspend fun getVideoComments(
        videoId: String,
        beforeCommentId: Long,
        limit: Int? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/{videoId}/comments")
                .queryParam("beforeCommentId", beforeCommentId)
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
                .build(videoId)
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
        .awaitBody<Response<List<CoreVideoComment>>>().data!!

    suspend fun getTrendingVideos(
        days: Int,
        limit: Int? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/trending")
                .queryParam("days", days)
                .queryParamIfPresent("limit", Optional.ofNullable(limit))
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
        .awaitBody<Response<List<CoreVideo>>>().data!!

    suspend fun getLoyaltyPoints(
        userId: String,
        lastOffsetDate: Instant? = null,
        type: String? = null,
        month: Int? = null,
        year: Int? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$LOYALTY_ENDPOINT/member/{memberId}/points")
                .queryParamIfPresent("lastOffsetDate", Optional.ofNullable(lastOffsetDate?.toString()))
                .queryParamIfPresent("type", Optional.ofNullable(type))
                .queryParamIfPresent("month", Optional.ofNullable(month))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .build(userId)
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
        .awaitBody<Response<List<CoreLoyaltyPoint>>>().data!!

    suspend fun getMemberInfo(
        userId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$LOYALTY_ENDPOINT/member/{memberId}").build(userId)
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
        .awaitBody<Response<CoreMemberInfo>>().data!!

    suspend fun getUserVideoRewardProgresses(
        userId: String,
        videoIds: List<String>,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/videos/configs/users/{userId}")
                .queryParam("videoIds", videoIds.joinToString(","))
                .build(userId)
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
        .awaitBody<Response<List<CoreUserVideoRewardRecord>>>().data!!

    suspend fun getGiftDetail(
        giftId: Long,
        userId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$GIFT_ENDPOINT/{giftId}")
                .queryParam("userId", userId)
                .build(giftId)
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
        .awaitBody<Response<CoreGift>>().data!!

    suspend fun getAllGifts(
        userId: String? = null,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path(GIFT_ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent("userId", Optional.ofNullable(userId))
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("brandId", Optional.ofNullable(brandId))
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
        .awaitBody<Response<List<CoreGift>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getUserGifts(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$GIFT_ENDPOINT/users/{userId}")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("brandId", Optional.ofNullable(brandId))
                .build(userId)
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
        .awaitBody<Response<List<CoreGift>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun buyGift(
        userId: String,
        giftId: Long,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$GIFT_ENDPOINT/{giftId}/buy")
                .queryParam("userId", userId)
                .build(giftId)
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
        .awaitBody<Response<CoreGift>>().data!!

    suspend fun getBrands(
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder -> builder.path("$GIFT_ENDPOINT/brands").build() }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreGift.Brand>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getCategories() = webClient.get()
        .uri { builder -> builder.path("$GIFT_ENDPOINT/categories").build() }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreGift.Category>>>().data!!

    suspend fun reactMessage(
        messageId: Long,
        reactionId: Long,
        participantId: String,
        conversationId: Long,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$CONVERSATION_ENDPOINT/{conversationId}/messages/{messageId}/reactions/{reactionId}")
                .queryParam("participantId", participantId)
                .build(conversationId, messageId, reactionId)
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
        .awaitBody<Response<Nothing>>()

    suspend fun clearAllReactions(
        messageId: Long,
        participantId: String,
        conversationId: Long,
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$CONVERSATION_ENDPOINT/{conversationId}/messages/{messageId}/reactions")
                .queryParam("participantId", participantId)
                .build(conversationId, messageId)
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
        .awaitBody<Response<Nothing>>()


    suspend fun getAllReactionIcons() = webClient.get()
        .uri { builder -> builder.path("$CONVERSATION_ENDPOINT/react-icons").build() }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreMessageReactionIcon>>>().data!!

    suspend fun getBanners(
        page: Int,
        pageSize: Int,
        isActive: Boolean = true,
    ) = webClient.get()
        .uri { builder ->
            builder.path(BANNER_ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParam("isActive", isActive)
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
        .awaitBody<Response<List<CoreBanner>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun removeMessage(
        removeForSelfOnly: Boolean,
        messageId: Long,
        conversationId: Long,
        userId: String,
    ) = webClient.delete()
        .uri { builder ->
            builder.path("${CONVERSATION_ENDPOINT}/{conversationId}/messages/{messageId}")
                .queryParam("removeForSelfOnly", removeForSelfOnly)
                .queryParam("userId", userId)
                .build(conversationId, messageId)
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
        .awaitBody<Response<Nothing>>()

    suspend fun blockUser(
        userId: String,
        request: BlockUserRequest,
    ) = webClient.put()
        .uri { builder ->
            builder.path("${USERS_ENDPOINT}/{userId}/block")
                .build(userId)
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
        .awaitBody<Response<Nothing>>()

    suspend fun getBlackList(
        userId: String,
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("${USERS_ENDPOINT}/{userId}/black-list")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build(userId)
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
        .awaitBody<Response<List<SimpleUser>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun checkin(
        userId: String,
    ) = webClient.post()
        .uri { builder -> builder.path("${REWARD_ENDPOINT}/users/{userId}/checkin").build(userId) }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreRewardConfig>>().data!!

    suspend fun getMyCheckinProgresses(
        userId: String,
        localDate: LocalDate? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("${REWARD_ENDPOINT}/users/{userId}/checkin-progresses")
                .queryParamIfPresent("localDate", Optional.ofNullable(localDate))
                .build(userId)
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
        .awaitBody<Response<List<CoreCheckinProgress>>>().data!!

    suspend fun saveMessage(
        request: SaveMessageRequest,
        conversationId: Long,
    ) = createMessageInMultipleConversations(request.apply { this.conversationIds = listOf(conversationId) }).first()

    suspend fun createMessageInMultipleConversations(
        payload: SaveMessageRequest,
    ) = webClient.post()
        .uri { builder -> builder.path("${CONVERSATION_ENDPOINT}/messages").build() }
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
        .awaitBody<Response<List<CoreMessage>>>().data!!

    suspend fun getConversationDetails(
        userId: String,
        messageLimit: Int = 20,
        conversationIds: List<Long>,
    ) = webClient.get()
        .uri { builder ->
            builder.path("${CONVERSATION_ENDPOINT}/details")
                .queryParam("userId", userId)
                .queryParam("messageLimit", messageLimit)
                .queryParam("conversationIds", conversationIds.joinToString(","))
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

    suspend fun assignReferral(
        userId: String,
        payload: AssignReferralRequest,
    ) = webClient.put()
        .uri { builder ->
            builder.path("${USERS_ENDPOINT}/{userId}/referral").build(userId)
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
        .awaitBody<Response<Long>>().data!!

    suspend fun getConversationAttachments(
        conversationId: Long,
        attachmentTypes: List<Message.AttachmentType>,
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("${CONVERSATION_ENDPOINT}/{conversationId}/attachments")
                .queryParam("attachmentTypes", attachmentTypes.joinToString(","))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
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
        .awaitBody<Response<List<CoreAttachment>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getPromotedVideos(
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/promoted")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
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
        .awaitBody<Response<List<CorePromotedVideoConfig>>>().data!!

    suspend fun reactToPromotedVideo(
        videoId: String,
        youtubeToken: String,
        userId: String
    ) = webClient.post()
        .uri { builder ->
            builder.path("$MEDIA_ENDPOINT/videos/{videoId}/react-promote").build(videoId)
        }
        .bodyValue(CoreReactToPromotedVideoReq(
            youtubeToken = youtubeToken, userId = userId
        ))
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

    suspend fun getCoinToPointExchangeRate() = webClient.get()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ENDPOINT/rate").build()
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
        .awaitBody<Response<Double>>().data!!

    suspend fun exchangePointToCoin(
        points: Long,
        userId: String,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ENDPOINT/users/{userId}/convert").build(userId)
        }
        .bodyValue(CoreExchangePointRequest(points))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreCoinTransaction>>().data!!

    suspend fun getUserCoinBalance(
        userId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ENDPOINT/users/{userId}/balance").build(userId)
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
        .awaitBody<Response<CoreCoinBalance>>().data!!

    suspend fun getUserCoinTransactions(
        userId: String,
        lastOffsetDate: Instant? = null,
        type: String? = null,
        month: Int? = null,
        year: Int? = null,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ENDPOINT/users/{userId}/transactions")
                .queryParamIfPresent("lastOffsetDate", Optional.ofNullable(lastOffsetDate?.toString()))
                .queryParamIfPresent("type", Optional.ofNullable(type))
                .queryParamIfPresent("month", Optional.ofNullable(month))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .build(userId)
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
        .awaitBody<Response<List<CoreCoinTransaction>>>().data!!
}
