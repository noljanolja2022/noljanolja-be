package com.noljanolja.server.admin.adapter.core


import com.noljanolja.server.admin.model.CoreServiceError
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.common.rest.Response
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono


@Component
class CoreApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val USERS_ENDPOINT = "/api/v1/users"
        const val STICKER_PACK_ENDPOINT = "/api/v1/media/sticker-packs"
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
            it.bodyToMono<Response<Nothing>>().mapNotNull { _ ->
                CoreServiceError.UserNotFound
            }
        }
        .awaitBody<Response<CoreUser>>().data

    suspend fun createStickerPack(
        stickerPack: StickerPack
    ) : Long? = webClient.post()
        .uri { builder -> builder.path(STICKER_PACK_ENDPOINT).build()}
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
}