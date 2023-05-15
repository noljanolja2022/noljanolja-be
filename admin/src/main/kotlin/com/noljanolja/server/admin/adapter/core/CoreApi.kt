package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.admin.model.CoreServiceError
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.admin.model.Video
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
    }

    suspend fun getUser(
        userId: String,
    ): CoreUser? = webClient.get()
        .uri { builder ->
            builder.path("$USERS_ENDPOINT/{userId}").build(userId)
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
        .awaitBody<Response<CoreUser>>().data

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
}