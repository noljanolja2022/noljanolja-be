package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.admin.adapter.core.request.CoreUpsertChatConfigRequest
import com.noljanolja.server.admin.adapter.core.request.CoreUpsertVideoConfigRequest
import com.noljanolja.server.admin.model.*
import com.noljanolja.server.admin.rest.request.CoinExchangeReq
import com.noljanolja.server.admin.rest.request.UpsertCheckinConfigRequest
import com.noljanolja.server.common.rest.Response
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
import java.util.*

@Component
class CoreApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val USERS_ENDPOINT = "/api/v1/users"
        const val STICKER_PACK_ENDPOINT = "/api/v1/media/sticker-packs"
        const val VIDEO_ENDPOINT = "/api/v1/media/videos"
        const val REWARD_ENDPOINT = "/api/v1/reward"
        const val COIN_EXCHANGE_ROUTE = "/api/v1/coin-exchange"
        const val BANNER_ENDPOINT = "/api/v1/banners"
    }

    suspend fun getUsers(
        page: Int = 1,
        pageSize: Int = 10,
        query: String? = null,
    ): Response<List<CoreUser>> = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent(
                    "query",
                    Optional.ofNullable(query?.let { UriUtils.encode(it, StandardCharsets.UTF_8) })
                )
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
        .awaitBody<Response<List<CoreUser>>>()

    suspend fun createUser(
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

    suspend fun updateUser(
        userId: String,
        data: CoreUserUpdateReq
    ) = webClient.patch()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/$userId").build()
        }
        .bodyValue(data)
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

    suspend fun deleteUser(
        userId: String,
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/$userId").build()
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

    suspend fun getStickerPacks() =
        webClient.get()
            .uri { builder -> builder.path(STICKER_PACK_ENDPOINT).build() }
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                    CoreServiceError.CoreServiceBadRequest(response.message)
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.just(CoreServiceError.CoreServiceInternalError)
            }
            .awaitBody<Response<List<StickerPack>>>()

    suspend fun createStickerPack(
        stickerPack: StickerPack
    ): Long? = webClient.post()
        .uri { builder -> builder.path(STICKER_PACK_ENDPOINT).build() }
        .bodyValue(stickerPack)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Long>>().data

    suspend fun deleteStickerPack(
        stickerPackId: String
    ) = webClient.delete()
        .uri { builder -> builder.path("$STICKER_PACK_ENDPOINT/$stickerPackId").build() }
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

    suspend fun getVideoAnalytics(
        page: Int,
        pageSize: Int
    ) = webClient.get()
        .uri { builder ->
            builder.path("$VIDEO_ENDPOINT/analytics")
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
        .awaitBody<Response<List<TrackInfo>>>()


    suspend fun getVideo(
        query: String? = null,
        page: Int,
        pageSize: Int
    ) = webClient.get()
        .uri { builder ->
            builder.path(VIDEO_ENDPOINT)
                .queryParamIfPresent("query", Optional.ofNullable(query))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParam("includeDeleted", true)
                .queryParam("includeUnavailableVideos", true)
                .queryParam("includeDeactivated", true)
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
        .awaitBody<Response<List<Video>>>()

    suspend fun getVideoDetail(
        videoId: String
    ) = webClient.get()
        .uri { builder ->
            builder.path("$VIDEO_ENDPOINT/$videoId")
                .queryParam("includeDeleted", true)
                .queryParam("includeUnavailableVideos", true)
                .queryParam("includeDeactivated", true)
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
        .awaitBody<Response<Video>>().data!!

    suspend fun importVideo(
        videoId: String,
        youtubeUrl: String,
        isHighlighted: Boolean,
        availableFrom: Instant? = null,
        availableTo: Instant? = null,
    ) = webClient.post()
        .uri { builder -> builder.path(VIDEO_ENDPOINT).build() }
        .bodyValue(CoreCreateVideoRequest(videoId, youtubeUrl, isHighlighted, availableFrom, availableTo))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Video>>().data

    suspend fun deleteVideo(
        videoId: String
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$VIDEO_ENDPOINT/${videoId}")
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
        .awaitBody<Response<Nothing>>()

    suspend fun getPromotedVideos() = webClient.get()
        .uri { builder ->
            builder.path("$VIDEO_ENDPOINT/promoted")
                .queryParam("includeDeleted", true)
                .queryParam("includeUnavailableVideos", true)
                .queryParam("includeDeactivated", true)
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
        .awaitBody<Response<List<PromotedVideoConfig>>>()

    suspend fun updatePromotedVideo(
        videoId: String,
        promoteVideoRequest: PromoteVideoRequest
    ) = webClient.post()
        .uri { builder ->
            builder.path("$VIDEO_ENDPOINT/$videoId/promote")
                .build()
        }
        .bodyValue(promoteVideoRequest)
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

    suspend fun getVideosRewardConfigs(
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/videos/configs")
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
        .awaitBody<Response<List<CoreVideoRewardConfig>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getVideoRewardConfig(
        configId: Long,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/videos/configs/{configId}")
                .build(configId)
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
        .awaitBody<Response<CoreVideoRewardConfig>>().data!!

    suspend fun getVideoRewardConfig(
        videoId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("/api/v2/reward/videos/configs/{videoId}")
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
        .awaitBody<Response<CoreVideoRewardConfig>>().data

    suspend fun upsertVideoRewardConfigs(
        payload: CoreUpsertVideoConfigRequest,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/videos/configs")
                .build()
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
        .awaitBody<Response<CoreVideoRewardConfig>>().data!!

    suspend fun deleteVideoRewardConfigs(
        configId: Long,
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/videos/configs/{configId}")
                .build(configId)
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

    suspend fun getChatConfigs(
        roomType: String?
    ) = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/chat/configs")
                .queryParamIfPresent("roomType", Optional.ofNullable(roomType))
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
        .awaitBody<Response<List<CoreChatRewardConfig>>>().data!!

    suspend fun upsertChatConfig(
        payload: CoreUpsertChatConfigRequest,
    ) = webClient.put()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/chat/configs")
                .build()
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
        .awaitBody<Response<CoreChatRewardConfig>>().data!!

    suspend fun getCheckinConfig(

    ) = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/checkin/configs")
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
        .awaitBody<Response<List<CoreCheckinConfig>>>().data!!

    suspend fun updateCheckinConfig(
        payload: UpsertCheckinConfigRequest
    ) = webClient.post()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/checkin/configs")
                .build()
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
        .awaitBody<Response<List<CoreCheckinConfig>>>().data!!

    suspend fun getBanners(
        name: String?,
        page: Int,
        pageSize: Int,
        isActive: Boolean?,
    ) = webClient.get()
        .uri { builder ->
            builder.path(BANNER_ENDPOINT)
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent("isActive", Optional.ofNullable(isActive))
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
        .awaitBody<Response<List<CoreBanner>>>()

    suspend fun updateBanner(
        payload: UpsertBannerRequest
    ) = webClient.put()
        .uri { builder ->
            builder.path(BANNER_ENDPOINT)
                .build()
        }
        .bodyValue(payload)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.GeneralApiError(response.code, response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreBanner>>()

    suspend fun deleteBanner(
        id: Long
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$BANNER_ENDPOINT/$id")
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
        .awaitBody<Response<Nothing>>()

    suspend fun getReferralConfig() = webClient.get()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/referral/configs").build()
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
        .awaitBody<Response<ReferralConfig>>()

    suspend fun updateReferralConfig(payload: UpsertReferralConfigReq) = webClient.put()
        .uri { builder ->
            builder.path("$REWARD_ENDPOINT/referral/configs").build()
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
        .awaitBody<Response<ReferralConfig>>().data!!

    suspend fun getCoinExchangeConfig() = webClient.get()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ROUTE/rate").build()
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
        .awaitBody<Response<CoinExchangeConfig>>().data!!

    suspend fun updateCoinExchangeConfig(payload: CoinExchangeReq) = webClient.put()
        .uri { builder ->
            builder.path("$COIN_EXCHANGE_ROUTE/rate").build()
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
        .awaitBody<Response<CoinExchangeConfig>>().data!!

    suspend fun upsertVideoGeneratedComments(
        comments: List<String>,
        videoId: String,
    ) = webClient.post()
        .uri { uriBuilder -> uriBuilder.path("$VIDEO_ENDPOINT/{videoId}/generated-comments").build(videoId) }
        .bodyValue(comments)
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
}