package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.admin.adapter.core.request.CoreUpsertChatConfigRequest
import com.noljanolja.server.admin.adapter.core.request.CoreUpsertVideoConfigRequest
import com.noljanolja.server.admin.model.*
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
        const val GIFT_ROUTES = "/api/v1/gifts"
        const val BANNER_ENDPOINT = "/api/v1/banners"
    }

    suspend fun getUsers(
        page: Int = 1,
        pageSize: Int = 10,
        phoneNumber: String? = null,
    ): Response<List<CoreUser>> = webClient.get()
        .uri { builder ->
            builder.path(USERS_ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent(
                    "phoneNumber",
                    Optional.ofNullable(phoneNumber?.let { UriUtils.encode(it, StandardCharsets.UTF_8) })
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
    )   = webClient.patch()
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

    suspend fun getVideo(
        page: Int,
        pageSize: Int
    ) = webClient.get()
        .uri { builder ->
            builder.path(VIDEO_ENDPOINT)
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
        .awaitBody<Response<List<Video>>>()

    suspend fun createVideo(
        request: CoreCreateVideoRequest
    ): Video? = webClient.post()
        .uri { builder -> builder.path(VIDEO_ENDPOINT).build() }
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

    suspend fun getGifts(
        categoryId: Long?,
        brandId: Long?,
        page: Int = 1,
        pageSize: Int = 10,
    ) = webClient.get()
        .uri { builder ->
            builder.path(GIFT_ROUTES)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
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
        .awaitBody<Response<List<Gift>>>().data!!

    suspend fun getGift(
        giftId: Long
    ) = webClient.get()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/$giftId").build()
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
        .awaitBody<Response<Gift>>().data!!

    suspend fun createGift(
        payload: CreateGiftRequest
    ) = webClient.post()
        .uri { builder ->
            builder.path(GIFT_ROUTES)
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
        .awaitBody<Response<Gift>>().data!!

    suspend fun updateGift(
        giftId: Long, payload: UpdateGiftRequest
    ) = webClient.patch()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/$giftId")
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
        .awaitBody<Response<Gift>>().data!!

    suspend fun deleteGift(
        giftId: Long
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/$giftId")
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

    suspend fun getBrands(
        page: Int, pageSize: Int
    ) = webClient.get()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/brands")
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
        .awaitBody<Response<List<Gift.Brand>>>().data!!

    suspend fun createBrand(
        payload: CreateBrandRequest
    ) = webClient.post()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/brands")
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
        .awaitBody<Response<Gift.Brand>>().data!!

    suspend fun updateBrand(
        brandId: Long, payload: UpdateBrandRequest
    ) = webClient.patch()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/brands/$brandId")
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
        .awaitBody<Response<Gift.Brand>>().data!!

    suspend fun deleteBrand(
        brandId: Long
    ) = webClient.delete()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/brands/$brandId")
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

    suspend fun getCategories(

    ) = webClient.get()
        .uri { builder ->
            builder.path("$GIFT_ROUTES/categories")
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
        .awaitBody<Response<List<Gift.Category>>>().data!!

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
                CoreServiceError.CoreServiceBadRequest(response.message)
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
}